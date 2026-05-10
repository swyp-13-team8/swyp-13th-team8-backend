package com.silsonfit.silsonfit_api.domain.insurance.repository;

import com.silsonfit.silsonfit_api.domain.insurance.entity.Insurance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 보험 상품 Repository
 */
public interface InsuranceRepository extends JpaRepository<Insurance, Long> {

    /**
     * 보험사명 + 세대로 상품 목록 조회
     */
    List<Insurance> findByCompanyNameAndGeneration(String companyName, int generation);
}
