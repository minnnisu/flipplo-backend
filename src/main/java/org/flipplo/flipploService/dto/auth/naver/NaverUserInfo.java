package org.flipplo.flipploService.dto.auth.naver;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

public class NaverUserInfo {
    @Getter
    @Setter
    public static class ResponseDetails {
        @JsonProperty("id")
        private String id;

        @JsonProperty("nickname")
        private String nickname;

        @JsonProperty("name")
        private String name;

        @JsonProperty("email")
        private String email;

        @JsonProperty("gender")
        private String gender;

        @JsonProperty("age")
        private String age;

        @JsonProperty("birthday")
        private String birthday;

        @JsonProperty("profile_image")
        private String profileImage;

        @JsonProperty("birthyear")
        private String birthyear;

        @JsonProperty("mobile")
        private String mobile;
    }

    @Getter
    @Setter
    public static class Response {
        @JsonProperty("resultcode")
        private String resultCode;

        @JsonProperty("message")
        private String message;

        @JsonProperty("response")
        private ResponseDetails responseDetails;
    }
}
