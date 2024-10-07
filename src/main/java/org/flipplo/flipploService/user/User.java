package org.flipplo.flipploService.user;

import jakarta.persistence.*;
import lombok.*;
import org.flipplo.flipploService.constant.SnsType;
import org.flipplo.flipploService.dto.auth.signup.Signup;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Entity
@Table(name = "Users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User implements OAuth2User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String snsId;

    @Column(nullable = false, unique = true)
    private SnsType snsType;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Double weight;

    private String profileImage;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private String authority;

    public static User of(String snsId, SnsType snsType, String profileImage, Signup.Request requestDto) {
        return User.builder()
                .snsId(snsId)
                .snsType(snsType)
                .profileImage(profileImage)
                .nickname(requestDto.getNickname())
                .email(requestDto.getEmail())
                .weight(requestDto.getWeight())
                .build();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton((GrantedAuthority) () -> authority);
    }

    @Override
    public String getName() {
        return nickname;
    }
}
