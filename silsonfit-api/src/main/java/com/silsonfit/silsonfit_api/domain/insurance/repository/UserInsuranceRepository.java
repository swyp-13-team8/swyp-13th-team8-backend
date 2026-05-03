package com.silsonfit.silsonfit_api.domain.insurance.repository;

import com.silsonfit.silsonfit_api.domain.insurance.entity.UserInsurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 사용자 보험 등록 Repository
 */
public interface UserInsuranceRepository extends JpaRepository<UserInsurance, Long> {

    /**
     * 사용자의 등록 보험 목록 조회 (Insurance 함께 로딩)
     */
    @Query("SELECT ui FROM UserInsurance ui JOIN FETCH ui.insurance WHERE ui.userId = :userId")
    List<UserInsurance> findByUserIdWithInsurance(@Param("userId") Long userId);

    /**
     * 사용자의 등록 보험 개수 조회
     */
    long countByUserId(Long userId);
}
