package com.silsonfit.silsonfit_api.domain.calculation.policy;

import com.silsonfit.silsonfit_api.domain.calculation.enums.InsuranceGeneration;
import com.silsonfit.silsonfit_api.domain.calculation.enums.PurposeType;
import com.silsonfit.silsonfit_api.domain.calculation.enums.TreatmentCategory;
import com.silsonfit.silsonfit_api.domain.calculation.enums.VisitType;
import com.silsonfit.silsonfit_api.domain.calculation.vo.CalculationResult;

/**
 * 실손 보험 세대별 계산 정책 인터페이스
 * - 1~4세대 실손보험의 계산 공식을 추상화
 * - CalculationService는 세대별 구현체를 알 필요 없이 이 인터페이스로 계산 수행
 *
 */
public interface InsuranceGenerationPolicy {

    /**
     * 해당 Policy가 처리 가능한 보험 세대인지 판단
     *
     * @param generation 보험 세대
     * @return 지원 여부
     */
    boolean supports(InsuranceGeneration generation);

    /**
     * 세대별 실손 보험 계산 수행
     *
     * @param insuranceId 보험 ID
     * @param medicalCost 의료비 입력값
     * @param ediCode EDI 코드
     * @param visitType 진료 유형
     * @param treatmentCategory 진료 항목
     * @param purposeType 진료 목적
     * @return 계산 결과
     */
    CalculationResult calculate(
            Long insuranceId,
            Integer medicalCost,
            String ediCode,
            VisitType visitType,
            TreatmentCategory treatmentCategory,
            PurposeType purposeType
    );
}