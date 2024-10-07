package org.flipplo.flipploService.controller.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.flipplo.flipploService.dto.auth.login.Login;
import org.flipplo.flipploService.dto.auth.logout.Logout;
import org.flipplo.flipploService.dto.auth.signup.NicknameDuplicationCheck;
import org.flipplo.flipploService.dto.auth.signup.Signup;
import org.flipplo.flipploService.dto.auth.tokenReissue.TokenReIssue;
import org.flipplo.flipploService.service.auth.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Login.Response> loginNaver(@Valid @RequestBody Login.Request requestDto) {
        Login.Dto dto = authService.login(requestDto);
        return new ResponseEntity<>(Login.Response.fromDto(dto), HttpStatus.CREATED);
    }

    @PostMapping("/signup")
    public ResponseEntity<Signup.Response> signup(@Valid @RequestBody Signup.Request requestDto) {
        Signup.Dto dto = authService.signup(requestDto);
        return new ResponseEntity<>(Signup.Response.fromDto(dto), HttpStatus.CREATED);
    }

    @PostMapping("/nickname/duplicationCheck")
    public ResponseEntity<NicknameDuplicationCheck.Response> checkNicknameDuplication(@Valid @RequestBody NicknameDuplicationCheck.Request requestDto) {
        authService.checkNickname(requestDto);
        return new ResponseEntity<>(NicknameDuplicationCheck.Response.success(), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Logout.Response> logout(
            @RequestHeader("Authorization-refresh") String refreshToken
    ) {
        authService.logout(refreshToken);

        return new ResponseEntity<>(Logout.Response.success(), HttpStatus.CREATED);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<TokenReIssue.Response> reIssueToken(
            @RequestHeader("Authorization") String accessToken,
            @RequestHeader("Authorization-refresh") String refreshToken
    ){
        TokenReIssue.Dto dto = authService.reIssueToken(accessToken, refreshToken);
        return new ResponseEntity<>(TokenReIssue.Response.fromDto(dto), HttpStatus.CREATED);
    }
}
