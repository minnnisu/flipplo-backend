package org.flipplo.flipploService.dto.auth.signup;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.flipplo.flipploService.constant.SnsType;
import org.flipplo.flipploService.user.User;;

public class Signup {
    @Getter
    @Setter
    public static class Request {
        @NotNull(message = "소셜 계정의 타입이 필요합니다.")
        private SnsType snsType;

        @NotNull(message = "accessToken을 기입해주세요")
        private String accessToken;

        @Size(min = 2, max = 20, message = "닉네임의 길이는 2에서 20자 사이여야 합니다")
        @NotNull(message = "닉네임이 필요합니다")
        private String nickname;

        @Email(message = "유효하지 않은 이메일입니다.")
        @Size(min = 2, max = 254, message = "이메일의 길이는 2에서 254자 사이여야 합니다")
        @NotNull(message = "이메일이 필요합니다")
        private String email;

        @Min(value = 0, message = "몸무게는 0kg보다 커야 합니다.")
        @Max(value = 200, message = "몸무게는 200kg보다 작아야 합니다.")
        @NotNull(message = "몸무게가 필요합니다.")
        private Double weight;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Dto {
        private Long userId;
        private String snsId;
        private SnsType snsType;
        private String nickname;
        private String email;
        private Double weight;

        public static Dto fromEntity(User user) {
            return Dto.builder()
                    .userId(user.getId())
                    .snsId(user.getSnsId())
                    .snsType(user.getSnsType())
                    .nickname(user.getNickname())
                    .email(user.getEmail())
                    .weight(user.getWeight())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private Long userId;
        private String snsId;
        private SnsType snsType;
        private String nickname;
        private String email;
        private Double weight;

        public static UserInfo fromDto(Dto dto) {
            return UserInfo.builder()
                    .userId(dto.getUserId())
                    .snsId(dto.getSnsId())
                    .snsType(dto.getSnsType())
                    .nickname(dto.getNickname())
                    .email(dto.getEmail())
                    .weight(dto.getWeight())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String message;
        private UserInfo userInfo;

        public static Response fromDto(Dto dto) {
            return Response.builder()
                    .message("회원가입 되었습니다.")
                    .userInfo(UserInfo.fromDto(dto))
                    .build();
        }
    }
}
