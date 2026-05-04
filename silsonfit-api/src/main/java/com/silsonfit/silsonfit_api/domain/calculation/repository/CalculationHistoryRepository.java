package com.silsonfit.silsonfit_api.domain.calculation.repository;

import com.silsonfit.silsonfit_api.domain.calculation.entity.CalculationHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 실손 보험 계산 이력 Repository
 */
public interface CalculationHistoryRepository extends JpaRepository<CalculationHistory, Long> {

    /**
     * 사용자별 계산 이력 최신순 조회
     */
    Page<CalculationHistory> findByUserIdOrderByCreatedAtDescIdDesc(Long userId, Pageable pageable);
}
