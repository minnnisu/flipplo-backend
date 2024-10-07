package org.flipplo.flipploService.dto.auth.naver;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaverUserInfoResponseField {
    private String id;                // 동일인 식별 정보 (네이버 아이디마다 고유값)

    private String name;              // 사용자 이름

    @JsonProperty("profile_image")
    private String profileImage;      // 사용자 프로필 사진 URL
}
