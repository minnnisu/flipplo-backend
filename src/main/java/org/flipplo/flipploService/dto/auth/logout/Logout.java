package org.flipplo.flipploService.dto.auth.logout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class Logout {
    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String message;

        public static Response success() {
            return Response.builder()
                    .message("로그아웃 되었습니다.")
                    .build();
        }
    }
}
