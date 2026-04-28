package com.silsonfit.silsonfit_api.domain.calculation.repository;

import com.silsonfit.silsonfit_api.domain.calculation.entity.CoverageRule;
import com.silsonfit.silsonfit_api.domain.calculation.enums.PurposeType;
import com.silsonfit.silsonfit_api.domain.calculation.enums.TreatmentCategory;
import com.silsonfit.silsonfit_api.domain.calculation.enums.VisitType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 보험별 보장 룰 Repository
 *
 */
public interface CoverageRuleRepository extends JpaRepository<CoverageRule, Long> {

    /**
     * EDI 코드 기반 보장 룰 조회
     */
    Optional<CoverageRule> findByInsuranceIdAndEdiCode(
            Long insuranceId,
            String ediCode
    );

    /**
     * 진료 유형/항목/목적 기반 보장 룰 조회
     */
    Optional<CoverageRule> findByInsuranceIdAndVisitTypeAndTreatmentCategoryAndPurposeType(
            Long insuranceId,
            VisitType visitType,
            TreatmentCategory treatmentCategory,
            PurposeType purposeType
    );
}