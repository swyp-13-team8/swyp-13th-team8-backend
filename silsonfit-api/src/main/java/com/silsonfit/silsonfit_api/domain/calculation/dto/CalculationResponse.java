package com.silsonfit.silsonfit_api.domain.calculation.dto;

import com.silsonfit.silsonfit_api.domain.calculation.vo.CalculationResult;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 실손 보험 계산 응답 DTO
 */
@Getter
@Builder
public class CalculationResponse {

    /** 보장 여부 */
    private Boolean isCovered;

    /** 예상 환급액 */
    private Integer refundAmount;

    /** 총 자기부담금 */
    private Integer deductibleAmount;

    /** 계산 근거 */
    private List<String> basis;

    /** 면책/주의사항 */
    private String disclaimer;

    /** VO → DTO 변환 */
    public static CalculationResponse from(CalculationResult result) {
        return CalculationResponse.builder()
                .isCovered(result.getIsCovered())
                .refundAmount(result.getRefundAmount())
                .deductibleAmount(result.getDeductibleAmount())
                .basis(result.getBasis())
                .disclaimer(result.getDisclaimer())
                .build();
    }
}