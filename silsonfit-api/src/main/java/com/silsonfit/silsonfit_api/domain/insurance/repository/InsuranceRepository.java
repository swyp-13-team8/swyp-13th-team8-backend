package com.silsonfit.silsonfit_api.domain.insurance.repository;

import com.silsonfit.silsonfit_api.domain.insurance.entity.Insurance;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 보험 상품 Repository
 */
public interface InsuranceRepository extends JpaRepository<Insurance, Long> {
}
