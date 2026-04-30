package com.silsonfit.silsonfit_api.domain.calculation.service;

import com.silsonfit.silsonfit_api.domain.calculation.entity.CoverageRule;
import com.silsonfit.silsonfit_api.domain.calculation.entity.EdiCode;
import com.silsonfit.silsonfit_api.domain.calculation.enums.FeeType;
import com.silsonfit.silsonfit_api.domain.calculation.enums.PurposeType;
import com.silsonfit.silsonfit_api.domain.calculation.enums.TreatmentCategory;
import com.silsonfit.silsonfit_api.domain.calculation.enums.VisitType;
import com.silsonfit.silsonfit_api.domain.calculation.policy.CoverageRuleGenerationPolicy;
import com.silsonfit.silsonfit_api.domain.calculation.vo.CoverageRuleContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 보장 룰 생성기
 *
 * - 외부 수가 API 응답을 저장한 EDI 코드 정보를 기반으로 보장 룰을 생성한다.
 * - 보험 세대/보험 ID별 보장 정책은 CoverageRuleGenerationPolicy 구현체가 결정한다.
 * TODO(calculation): 실제 보험 약관 DB가 생기면 CoverageRuleGenerationPolicy 구현체에서 DB 정책을 조회하도록 확장한다.
 */
@Component
@RequiredArgsConstructor
public class CoverageRuleGenerator {

    private final CoverageRuleGenerationPolicyResolver policyResolver;

    /**
     * EDI 코드 기반 기본 보장 룰 생성
     *
     * @param context 보험 조건
     * @param ediCode EDI 코드
     * @return 생성된 보장 룰
     */
    public CoverageRule generate(
            CoverageRuleContext context,
            EdiCode ediCode
    ) {
        CoverageRuleGenerationPolicy policy = policyResolver.resolve(context, ediCode);
        boolean isCovered = policy.isCovered(context, ediCode);

        return CoverageRule.create(
                context.insuranceId(),
                ediCode.getCode(),
                resolveVisitType(ediCode),
                resolveTreatmentCategory(ediCode),
                resolvePurposeType(ediCode),
                isCovered,
                policy.coverageRate(context, ediCode),
                policy.deductibleAmount(context, ediCode),
                policy.limitAmount(context, ediCode),
                createBasis(context, ediCode, isCovered),
                policy.disclaimer(context, ediCode)
        );
    }

    /**
     * EDI 수가 유형 기반 진료 유형 결정
     */
    private VisitType resolveVisitType(EdiCode ediCode) {
        if (ediCode.getFeeType() == FeeType.PHARMACY) {
            return VisitType.MEDICATION;
        }

        return VisitType.OUTPATIENT;
    }

    /**
     * EDI 명칭/분류번호 기반 진료 항목 결정
     */
    private TreatmentCategory resolveTreatmentCategory(EdiCode ediCode) {
        if (ediCode.containsKeyword("MRI") || ediCode.containsKeyword("자기공명")) {
            return TreatmentCategory.MRI;
        }
        if (ediCode.containsKeyword("CT") || ediCode.containsKeyword("전산화단층")) {
            return TreatmentCategory.CT;
        }
        if (ediCode.containsKeyword("도수")) {
            return TreatmentCategory.MANUAL_THERAPY;
        }
        if (ediCode.containsKeyword("충격파")) {
            return TreatmentCategory.SHOCKWAVE_THERAPY;
        }
        if (ediCode.containsKeyword("주사")) {
            return TreatmentCategory.INJECTION;
        }
        if (ediCode.containsKeyword("물리")) {
            return TreatmentCategory.PHYSICAL_THERAPY;
        }

        return TreatmentCategory.GENERAL_TREATMENT;
    }

    /**
     * EDI 명칭 기반 진료 목적 결정
     */
    private PurposeType resolvePurposeType(EdiCode ediCode) {
        if (ediCode.containsKeyword("검진") || ediCode.containsKeyword("건강검진")) {
            return PurposeType.CHECKUP;
        }

        return PurposeType.TREATMENT;
    }

    /**
     * EDI 코드 원본 응답 필드를 계산 근거로 생성
     */
    private List<String> createBasis(
            CoverageRuleContext context,
            EdiCode ediCode,
            boolean isCovered
    ) {
        return List.of(
                String.format(
                        "보험세대 %s, 보장여부 %s 기준 보장 룰",
                        context.generation().getDescription(),
                        isCovered ? "보장" : "비보장"
                ),
                String.format(
                        "EDI 코드 %s, 한글명 %s, 수가분류번호 %s, 급여구분 %s, 수가유형 %s, 단가 %s원, 상대가치점수 %s, 적용시작일자 %s 기준",
                        ediCode.getCode(),
                        ediCode.getTreatmentName(),
                        formatNullable(ediCode.getFeeDivisionNumber()),
                        ediCode.getPayType().getDescription(),
                        ediCode.getFeeType().getDescription(),
                        formatNullable(ediCode.getUnitPrice()),
                        formatNullable(ediCode.getRelativeValuePoint()),
                        formatNullable(ediCode.getEffectiveStartDate())
                )
        );
    }

    private String formatNullable(Object value) {
        if (value == null) {
            return "-";
        }

        return value.toString();
    }
}
