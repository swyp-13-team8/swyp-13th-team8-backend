package com.silsonfit.silsonfit_api.domain.calculation.repository;

import com.silsonfit.silsonfit_api.domain.calculation.entity.CalculationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 실손 보험 계산 이력 Repository
 */
public interface CalculationHistoryRepository extends JpaRepository<CalculationHistory, Long> {
}
