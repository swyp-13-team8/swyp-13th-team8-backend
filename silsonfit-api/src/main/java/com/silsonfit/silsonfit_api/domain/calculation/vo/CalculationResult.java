package com.silsonfit.silsonfit_api.domain.calculation.vo;

import lombok.*;

import java.util.List;

/**
 * 실손 보험 계산 결과 VO
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalculationResult {

    /** 보장 여부 */
    private Boolean isCovered;

    /** 예상 환급액 (원 단위) */
    private Integer refundAmount;

    /** 총 자기부담금 (원 단위) */
    private Integer deductibleAmount;

    /**
     * 계산 근거 목록
     * 예:
     * - "MRI 항목 보장률 30% 적용"
     * - "4세대 실손보험 기준 자기부담금 적용"
     */
    private List<String> basis;

    /** 면책 또는 주의사항 */
    private String disclaimer;

    /** 계산 결과 생성 */
    public static CalculationResult of(
            Boolean isCovered,
            Integer refundAmount,
            Integer deductibleAmount,
            List<String> basis,
            String disclaimer
    ) {
        return CalculationResult.builder()
                .isCovered(isCovered)
                .refundAmount(refundAmount)
                .deductibleAmount(deductibleAmount)
                .basis(basis)
                .disclaimer(disclaimer)
                .build();
    }
}