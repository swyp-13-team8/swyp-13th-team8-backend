package com.silsonfit.silsonfit_api.domain.auth.entity;

import com.silsonfit.silsonfit_api.domain.user.entity.User;
import com.silsonfit.silsonfit_api.global.common.BaseCreatedTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 리프레시 토큰 엔티티
 *
 * 로그인 시 발급되며 Access Token 재발급에 사용된다.
 * 사용자당 1개만 유지되고 재발급 시 Rotation 된다.
 */
@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseCreatedTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 토큰 소유 사용자 (사용자당 1개)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // 리프레시 토큰 값
    @Column(nullable = false, unique = true, length = 512)
    private String token;

    // 만료 시각
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Builder
    public RefreshToken(User user, String token, LocalDateTime expiresAt) {
        this.user = user;
        this.token = token;
        this.expiresAt = expiresAt;
    }

    /**
     * 토큰 값과 만료 시각을 갱신한다 (Rotation).
     *
     * @param token      새 토큰 값
     * @param expiresAt  새 만료 시각
     */
    public void updateToken(String token, LocalDateTime expiresAt) {
        this.token = token;
        this.expiresAt = expiresAt;
    }

    /**
     * 토큰이 만료되었는지 확인한다.
     */
    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }
}
