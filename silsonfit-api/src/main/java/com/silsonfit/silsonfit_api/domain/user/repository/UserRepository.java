package com.silsonfit.silsonfit_api.domain.user.repository;

import com.silsonfit.silsonfit_api.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 사용자 Repository
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 카카오 소셜 ID로 사용자를 조회한다.
     *
     * @param socialId 카카오 고유 ID
     * @return 사용자
     */
    Optional<User> findBySocialId(Long socialId);
}
