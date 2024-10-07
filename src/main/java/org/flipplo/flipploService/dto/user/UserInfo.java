package org.flipplo.flipploService.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.flipplo.flipploService.constant.SnsType;
import org.flipplo.flipploService.user.User;

import java.time.LocalDateTime;

public class UserInfo {
    @Getter
    @Builder
    @AllArgsConstructor
    public static class Dto {
        private String snsId;

        private SnsType snsType;

        private String nickname;

        private String email;

        private Double weight;

        private LocalDateTime createdAt;

        private LocalDateTime updatedAt;

        public static Dto fromEntity(User user) {
            return Dto.builder()
                    .snsId(user.getSnsId())
                    .snsType(user.getSnsType())
                    .nickname(user.getNickname())
                    .email(user.getEmail())
                    .weight(user.getWeight())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Response {
        private String snsId;

        private SnsType snsType;

        private String nickname;

        private String email;

        private Double weight;

        private LocalDateTime createdAt;

        private LocalDateTime updatedAt;

        public static Response fromDto(Dto dto) {
            return Response.builder()
                    .snsId(dto.getSnsId())
                    .snsType(dto.getSnsType())
                    .nickname(dto.getNickname())
                    .email(dto.getEmail())
                    .weight(dto.getWeight())
                    .createdAt(dto.getCreatedAt())
                    .updatedAt(dto.getUpdatedAt())
                    .build();
        }
    }

}
