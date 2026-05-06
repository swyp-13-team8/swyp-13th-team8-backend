package com.silsonfit.silsonfit_api.domain.user.repository;

import com.silsonfit.silsonfit_api.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 사용자 Repository
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 카카오 소셜 ID로 사용자 조회
     */
    Optional<User> findBySocialId(Long socialId);

    /**
     * 탈퇴 유예 만료 사용자 조회 (deactivatedAt이 지정 시각 이전인 사용자)
     */
    List<User> findByDeactivatedAtNotNullAndDeactivatedAtBefore(LocalDateTime dateTime);
}
