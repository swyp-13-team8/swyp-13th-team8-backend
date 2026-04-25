package com.silsonfit.silsonfit_api.domain.auth.repository;

import com.silsonfit.silsonfit_api.domain.auth.entity.RefreshToken;
import com.silsonfit.silsonfit_api.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * 토큰 값으로 RefreshToken 조회
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * 사용자로 RefreshToken 조회 (사용자당 1개)
     */
    Optional<RefreshToken> findByUser(User user);
}
