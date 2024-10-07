package org.flipplo.flipploService.dto.auth.tokenReissue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class TokenReIssue {
    @Getter
    @AllArgsConstructor
    @Builder
    public static class Dto {
        private String message;
        private String accessToken;

        public static Dto of(String reIssuedAccessToken) {
            return Dto.builder()
                    .message("토큰이 재발행되었습니다")
                    .accessToken(reIssuedAccessToken)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String message;
        private String accessToken;

        public static Response fromDto(Dto dto) {
            return Response.builder()
                    .message(dto.getMessage())
                    .accessToken(dto.accessToken)
                    .build();
        }
    }
}
