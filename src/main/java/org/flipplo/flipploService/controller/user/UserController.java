package org.flipplo.flipploService.controller.user;

import lombok.RequiredArgsConstructor;
import org.flipplo.flipploService.dto.user.UserInfo;
import org.flipplo.flipploService.user.User;
import org.flipplo.flipploService.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @GetMapping()
    public ResponseEntity<UserInfo.Response> getUserInfo(@AuthenticationPrincipal User user) {
        UserInfo.Dto userInfoDto = userService.getUserInfo(user);
        return new ResponseEntity<>(UserInfo.Response.fromDto(userInfoDto), HttpStatus.OK);
    }
}
