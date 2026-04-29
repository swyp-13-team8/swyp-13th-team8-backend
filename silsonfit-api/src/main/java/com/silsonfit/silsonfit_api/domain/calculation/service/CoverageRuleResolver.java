package com.silsonfit.silsonfit_api.domain.calculation.service;

import com.silsonfit.silsonfit_api.domain.calculation.entity.CoverageRule;
import com.silsonfit.silsonfit_api.domain.calculation.enums.PurposeType;
import com.silsonfit.silsonfit_api.domain.calculation.enums.TreatmentCategory;
import com.silsonfit.silsonfit_api.domain.calculation.enums.VisitType;
import com.silsonfit.silsonfit_api.domain.calculation.repository.CoverageRuleRepository;
import com.silsonfit.silsonfit_api.global.error.BusinessException;
import com.silsonfit.silsonfit_api.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 보장 룰 조회 Resolver
 *
 * - EDI 코드가 있으면 보험 ID + EDI 코드 기반 룰을 우선 조회한다.
 * - EDI 코드가 없거나 EDI 기반 룰이 없으면 진료 유형/항목/목적 기반 룰로 대체 조회한다.
 */
@Service
@RequiredArgsConstructor
public class CoverageRuleResolver {

    private final CoverageRuleRepository coverageRuleRepository;

    /**
     * 계산 요청 조건에 맞는 보장 룰 조회
     *
     * @param insuranceId 보험 ID
     * @param ediCode EDI 코드
     * @param visitType 진료 유형
     * @param treatmentCategory 진료 항목
     * @param purposeType 진료 목적
     * @return 계산에 사용할 보장 룰
     */
    public CoverageRule resolve(
            Long insuranceId,
            String ediCode,
            VisitType visitType,
            TreatmentCategory treatmentCategory,
            PurposeType purposeType
    ) {
        if (StringUtils.hasText(ediCode)) {
            return coverageRuleRepository.findByInsuranceIdAndEdiCode(insuranceId, ediCode.trim())
                    .orElseGet(() -> resolveByTreatmentInfo(
                            insuranceId,
                            visitType,
                            treatmentCategory,
                            purposeType
                    ));
        }

        return resolveByTreatmentInfo(
                insuranceId,
                visitType,
                treatmentCategory,
                purposeType
        );
    }

    /**
     * 진료 유형/항목/목적 기반 보장 룰 조회
     */
    private CoverageRule resolveByTreatmentInfo(
            Long insuranceId,
            VisitType visitType,
            TreatmentCategory treatmentCategory,
            PurposeType purposeType
    ) {
        return coverageRuleRepository.findByInsuranceIdAndVisitTypeAndTreatmentCategoryAndPurposeType(
                        insuranceId,
                        visitType,
                        treatmentCategory,
                        purposeType
                )
                .orElseThrow(() -> new BusinessException(ErrorCode.COVERAGE_RULE_NOT_FOUND));
    }
}
