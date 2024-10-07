package org.flipplo.flipploService.provider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.flipplo.flipploService.config.ErrorCode;
import org.flipplo.flipploService.constant.TokenType;
import org.flipplo.flipploService.user.User;
import org.flipplo.flipploService.exception.CustomErrorException;
import org.flipplo.flipploService.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;
    private final UserRepository userRepository;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, UserRepository userRepository) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.userRepository = userRepository;
    }

    // 유저 정보를 가지고 AccessToken, RefreshToken 을 생성하는 메서드
    public String generateAccessToken(String snsId) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(snsId)); // subject
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims) //정보 저장
                .setIssuedAt(now) //토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + accessTokenExpirationPeriod))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken() {
        Date now = new Date();

        // Refresh Token 생성
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setExpiration(new Date(now.getTime() + refreshTokenExpirationPeriod))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        // Claim: 사용자에 대한 프로퍼티나 속성
        Claims claims = parseClaims(accessToken);

        if (claims.get("sub") == null) {
            throw new CustomErrorException(ErrorCode.NotValidAccessTokenError);
        }

        String snsId =  claims.get("sub").toString();
        User users = userRepository.findBySnsId(snsId).orElseThrow(() -> new CustomErrorException(ErrorCode.UserNotFoundError));

        return new UsernamePasswordAuthenticationToken(users, accessToken, null);
    }

    // 토큰 정보를 검증하는 메서드
    public void validateToken(TokenType tokenType, String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (SecurityException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            if (tokenType == TokenType.ACCESS_TOKEN) throw new CustomErrorException(ErrorCode.NotValidAccessTokenError);
            if (tokenType == TokenType.REFRESH_TOKEN) throw new CustomErrorException(ErrorCode.NotValidRefreshTokenError);
        } catch (ExpiredJwtException e) {
            if (tokenType == TokenType.ACCESS_TOKEN) throw new CustomErrorException(ErrorCode.ExpiredAccessTokenError);
            if (tokenType == TokenType.REFRESH_TOKEN) throw new CustomErrorException(ErrorCode.ExpiredRefreshTokenError);
        }
    }

    public boolean isExpiredToken(TokenType tokenType, String token) {
        try {
            validateToken(tokenType, token);
        } catch (CustomErrorException e) {
            if (e.getErrorCode() == ErrorCode.ExpiredAccessTokenError || e.getErrorCode() == ErrorCode.ExpiredRefreshTokenError) {
                return true;
            }
            throw e;
        }
        return false;
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String resolveToken(String value) {
        if (StringUtils.hasText(value) && value.startsWith("Bearer")) {
            return value.substring(7);
        }

        return null;
    }

    public String reIssueAccessToken(String accessToken) {
        Authentication authentication = getAuthentication(accessToken);
        Claims claims = Jwts.claims().setSubject(String.valueOf(((User) authentication.getPrincipal()).getSnsId())); // subject
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims) //정보 저장
                .setIssuedAt(now) //토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + accessTokenExpirationPeriod))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
