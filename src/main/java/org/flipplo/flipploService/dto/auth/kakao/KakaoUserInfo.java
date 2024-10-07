package org.flipplo.flipploService.dto.auth.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;


public class KakaoUserInfo {
    @Getter
    @Setter
    public static class Response {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("connected_at")
        private ZonedDateTime connectedAt;

        @JsonProperty("properties")
        private Properties properties;

        @JsonProperty("kakao_account")
        private KakaoAccount kakaoAccount;
    }

    @Getter
    @Setter
    public static class Properties {
        @JsonProperty("nickname")
        private String nickname;

        @JsonProperty("profile_image")
        private String profileImage;

        @JsonProperty("thumbnail_image")
        private String thumbnailImage;
    }

    @Getter
    @Setter
    public static class KakaoAccount {
        @JsonProperty("profile_needs_agreement")
        private boolean profileNeedsAgreement;

        @JsonProperty("email_needs_agreement")
        private boolean emailNeedsAgreement;

        private UserProfile profile;
    }


    @Getter
    @Setter
    public static class UserProfile {
        @JsonProperty("nickname")
        private String nickname;

        @JsonProperty("thumbnail_image_url")
        private String thumbnailImageUrl;

        @JsonProperty("profile_image_url")
        private String profileImageUrl;

        @JsonProperty("is_default_image")
        private boolean isDefaultImage;

        @JsonProperty("is_default_nickname")
        private boolean isDefaultNickname;
    }

}