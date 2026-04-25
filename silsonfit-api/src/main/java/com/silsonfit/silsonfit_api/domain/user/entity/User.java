package com.silsonfit.silsonfit_api.domain.user.entity;

import com.silsonfit.silsonfit_api.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 엔티티
 *
 * 카카오 소셜 로그인 기반 회원 정보를 관리한다.
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 카카오 고유 ID
    @Column(nullable = false, unique = true)
    private Long socialId;

    // 사용자 이름 (카카오에서 가져옴)
    @Column(nullable = false)
    private String name;

    // 이메일 (카카오 선택 동의 항목, 미동의 시 null)
    @Column
    private String email;

    // 프로필 이미지 URL (카카오 선택 동의 항목, 미동의 시 null)
    @Column
    private String profileImageUrl;

    @Builder
    public User(Long socialId, String name, String email, String profileImageUrl) {
        this.socialId = socialId;
        this.name = name;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }
}
