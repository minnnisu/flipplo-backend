package org.flipplo.flipploService.service.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flipplo.flipploService.config.ErrorCode;
import org.flipplo.flipploService.constant.SnsType;
import org.flipplo.flipploService.constant.TokenType;
import org.flipplo.flipploService.dto.auth.kakao.KakaoUserInfo;
import org.flipplo.flipploService.dto.auth.login.Login;
import org.flipplo.flipploService.dto.auth.naver.NaverUserInfo;
import org.flipplo.flipploService.dto.auth.signup.NicknameDuplicationCheck;
import org.flipplo.flipploService.dto.auth.signup.Signup;
import org.flipplo.flipploService.dto.auth.tokenReissue.TokenReIssue;
import org.flipplo.flipploService.user.User;
import org.flipplo.flipploService.exception.CustomErrorException;
import org.flipplo.flipploService.provider.JwtTokenProvider;
import org.flipplo.flipploService.user.UserRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;

    public Login.Dto login(Login.Request requestDto) {
        String snsId = "";
        if(requestDto.getSnsType() == SnsType.Kakao){
            snsId = getKakaoUserSnsId(requestDto.getAccessToken());
        }

        if(requestDto.getSnsType() == SnsType.Naver){
            snsId = getNaverUserSnsId(requestDto.getAccessToken());
        }

        User user = userRepository.findBySnsId(snsId)
                .orElseThrow(() -> new CustomErrorException(ErrorCode.UserNotFoundError));

        String accessToken = jwtTokenProvider.generateAccessToken(user.getSnsId());
        String refreshToken = jwtTokenProvider.generateRefreshToken();

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(refreshToken, accessToken);

        return Login.Dto.of(accessToken, refreshToken);
    }

    public Signup.Dto signup(Signup.Request requestDto) {
        String profileImage = null;
        String snsId = null;
        SnsType snsType = null;

        if(requestDto.getSnsType() == SnsType.Kakao){
            KakaoUserInfo.Response kakaoUserInfo = getKakaoUserInfo(requestDto.getAccessToken());
            profileImage = kakaoUserInfo.getProperties().getProfileImage();
            snsId = String.valueOf(kakaoUserInfo.getId());
            snsType = SnsType.Kakao;
        }

        if(requestDto.getSnsType() == SnsType.Naver){
            NaverUserInfo.Response naverUserInfo = getNaverUserInfo(requestDto.getAccessToken());
            profileImage = naverUserInfo.getResponseDetails().getProfileImage();
            snsId = naverUserInfo.getResponseDetails().getId();
            snsType = SnsType.Naver;
        }

        Optional<User> userOptional = userRepository.findBySnsId(snsId);
        if(userOptional.isPresent()){
            throw new CustomErrorException(ErrorCode.AlreadyExistUserError);
        }

        checkNicknameDuplication(requestDto.getNickname());

        User newUser = userRepository.save(User.of(snsId, snsType, profileImage, requestDto));
        return Signup.Dto.fromEntity(newUser);
    }

    public void checkNickname(NicknameDuplicationCheck.Request requestDto) {
        checkNicknameDuplication(requestDto.getNickname());
    }


    public void logout(String refreshToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        String resolvedRefreshToken = jwtTokenProvider.resolveToken(refreshToken);

        if (resolvedRefreshToken == null) {
            throw new CustomErrorException(ErrorCode.NotValidRequestError);
        }

        String savedAccessToken = valueOperations.get(resolvedRefreshToken);
        if (savedAccessToken == null) {
            throw new CustomErrorException(ErrorCode.NoSuchRefreshTokenError);
        }

        valueOperations.getAndDelete(resolvedRefreshToken);
    }

    public TokenReIssue.Dto reIssueToken(String accessToken, String refreshToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        String resolvedAccessToken = jwtTokenProvider.resolveToken(accessToken);
        String resolvedRefreshToken = jwtTokenProvider.resolveToken(refreshToken);

        log.info("accessToken: " + resolvedAccessToken);
        log.info("refreshToken: " + resolvedRefreshToken);

        if (resolvedAccessToken == null || resolvedRefreshToken == null) {
            throw new CustomErrorException(ErrorCode.NotValidRequestError);
        }

        String savedAccessToken = valueOperations.get(resolvedRefreshToken);
        if (savedAccessToken == null) {
            throw new CustomErrorException(ErrorCode.NoSuchRefreshTokenError);
        }

        // RefreshToken 유효성 및 만료여부 확인
        boolean isExpiredRefreshToken = jwtTokenProvider.isExpiredToken(TokenType.REFRESH_TOKEN, resolvedRefreshToken);
        if (isExpiredRefreshToken) {
            valueOperations.getAndDelete(resolvedRefreshToken);
            throw new CustomErrorException(ErrorCode.ExpiredRefreshTokenError);
        }

        if (!resolvedAccessToken.equals(savedAccessToken)) {
            // RefreshToken이 탈취 당한 것으로 판단
            valueOperations.getAndDelete(resolvedRefreshToken);
            throw new CustomErrorException(ErrorCode.NoSuchAccessTokenError);
        }

        // AccessToken 유효성 및 만료여부 확인
        boolean isExpiredAccessToken = jwtTokenProvider.isExpiredToken(TokenType.ACCESS_TOKEN, resolvedAccessToken);
        if (!isExpiredAccessToken) {
            // RefreshToken이 탈취 당한 것으로 판단
            valueOperations.getAndDelete(resolvedRefreshToken);
            throw new CustomErrorException(ErrorCode.NotExpiredAccessTokenError);
        }

        String reIssuedAccessToken = jwtTokenProvider.reIssueAccessToken(resolvedAccessToken);
        valueOperations.getAndDelete(resolvedRefreshToken);
        valueOperations.set(resolvedRefreshToken, reIssuedAccessToken);
        return TokenReIssue.Dto.of(reIssuedAccessToken);
    }

    private String getNaverUserSnsId(String accessToken) {
        NaverUserInfo.Response userInfo = getNaverUserInfo(accessToken);
        return userInfo.getResponseDetails().getId();
    }

    private String getKakaoUserSnsId(String accessToken) {
        KakaoUserInfo.Response kakaoUserInfo = getKakaoUserInfo(accessToken);
        return String.valueOf(kakaoUserInfo.getId());
    }

    private void checkNicknameDuplication(String nickname){
        if(userRepository.findByNickname(nickname).isPresent()){
            throw new CustomErrorException(ErrorCode.DuplicatedNicknameError);
        }
    }

    private KakaoUserInfo.Response getKakaoUserInfo(String accessToken) {
        WebClient webClient = WebClient.builder().build();
        String url = "https://kapi.kakao.com/v2/user/me";

        return webClient.get()
                .uri(url)  // Replace with your actual URL
                .header(
                        "Authorization",
                        accessToken
                )
                .retrieve()
                .onStatus(status -> status.value() == 401,
                        this::handleKakao401Error)
                .onStatus(status -> status.value() == 403,
                        this::handleKakao403Error)
                .bodyToMono(KakaoUserInfo.Response.class)
                .block();
    }

    private NaverUserInfo.Response getNaverUserInfo(String accessToken) {
        WebClient webClient = WebClient.builder().build();


        return webClient.get()
                .uri("https://openapi.naver.com/v1/nid/me")  // Replace with your actual URL
                .header(
                        "Authorization",
                        accessToken
                )
                .retrieve()
                .onStatus(status -> status.value() == 401,
                        this::handleNaver401Error)
                .onStatus(status -> status.value() == 403,
                        this::handleNaver403Error)
                .bodyToMono(NaverUserInfo.Response.class)
                .block();
    }

    private Mono<? extends Throwable> handleKakao401Error(ClientResponse response) {
        return response.bodyToMono(String.class)
                .flatMap(errorBody -> Mono.error(new CustomErrorException(ErrorCode.UnauthorizedKakaoError)));
    }

    private Mono<? extends Throwable> handleKakao403Error(ClientResponse response) {
        return response.bodyToMono(String.class)
                .flatMap(errorBody -> Mono.error(new CustomErrorException(ErrorCode.ForbiddenKakaoError)));

    }

    private Mono<? extends Throwable> handleNaver401Error(ClientResponse response) {
        return response.bodyToMono(String.class)
                .doOnNext(errorBody -> log.error("Error Body: {}", errorBody))
                .flatMap(errorBody -> Mono.error(new CustomErrorException(ErrorCode.UnauthorizedNaverError)));
    }

    private Mono<? extends Throwable> handleNaver403Error(ClientResponse response) {
        return response.bodyToMono(String.class)
                .flatMap(errorBody -> Mono.error(new CustomErrorException(ErrorCode.ForbiddenNaverError)));

    }
}
