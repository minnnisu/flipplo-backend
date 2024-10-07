package org.flipplo.flipploService.dto.auth.signup;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class NicknameDuplicationCheck {
    @Getter
    @Setter
    public static class Request {
        @Size(min = 2, max = 20, message = "닉네임의 길이는 2에서 20자 사이여야 합니다")
        @NotNull(message = "닉네임이 필요합니다")
        private String nickname;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private String message;

        public static Response success() {
            return new Response("사용가능한 닉네임입니다.");
        }
    }
}
