package com.silsonfit.silsonfit_api.domain.calculation.repository;

import com.silsonfit.silsonfit_api.domain.calculation.entity.CoverageRule;
import com.silsonfit.silsonfit_api.domain.calculation.enums.InsuranceGeneration;
import com.silsonfit.silsonfit_api.domain.calculation.enums.PurposeType;
import com.silsonfit.silsonfit_api.domain.calculation.enums.TreatmentCategory;
import com.silsonfit.silsonfit_api.domain.calculation.enums.VisitType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 보장 룰 Repository
 *
 */
public interface CoverageRuleRepository extends JpaRepository<CoverageRule, Long> {

    /**
     * 보험 ID + EDI 코드 기반 보장 룰 조회
     */
    Optional<CoverageRule> findByInsuranceIdAndEdiCode(
            Long insuranceId,
            String ediCode
    );

    /**
     * 보험 ID + EDI 코드 기반 보장 룰 조회
     *
     * 중복 데이터가 있더라도 최신 룰 하나만 사용한다.
     */
    Optional<CoverageRule> findFirstByInsuranceIdAndEdiCodeOrderByIdDesc(
            Long insuranceId,
            String ediCode
    );

    /**
     * 보험 세대 + 진료 유형/항목/목적 기반 fallback 보장 룰 조회
     */
    Optional<CoverageRule> findByGenerationAndVisitTypeAndTreatmentCategoryAndPurposeType(
            InsuranceGeneration generation,
            VisitType visitType,
            TreatmentCategory treatmentCategory,
            PurposeType purposeType
    );

    /**
     * 보험/EDI 전용 룰이 아닌 세대 fallback 보장 룰 조회
     */
    List<CoverageRule> findByInsuranceIdIsNullAndEdiCodeIsNullAndGenerationAndVisitTypeAndTreatmentCategoryAndPurposeTypeOrderByIdAsc(
            InsuranceGeneration generation,
            VisitType visitType,
            TreatmentCategory treatmentCategory,
            PurposeType purposeType
    );
}
