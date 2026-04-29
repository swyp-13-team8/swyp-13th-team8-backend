package com.silsonfit.silsonfit_api.domain.calculation.service;

import com.silsonfit.silsonfit_api.domain.calculation.dto.CalculationRequest;
import com.silsonfit.silsonfit_api.domain.calculation.dto.CalculationResponse;
import com.silsonfit.silsonfit_api.domain.calculation.entity.CoverageRule;
import com.silsonfit.silsonfit_api.domain.calculation.vo.CalculationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 실손 보험 계산 Service
 *
 * - 요청 조건에 맞는 보장 룰을 조회한다.
 * - 조회된 보장 룰의 보장 여부, 보장률, 자기부담금, 한도를 기준으로 예상 환급액을 계산한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalculationService {

    private final CoverageRuleResolver coverageRuleResolver;

    /**
     * 실손 보험 예상 환급액 계산
     *
     * @param request 계산 요청
     * @return 계산 응답
     */
    public CalculationResponse calculate(CalculationRequest request) {
        CoverageRule coverageRule = coverageRuleResolver.resolve(
                request.getInsuranceId(),
                request.getEdiCode(),
                request.getVisitType(),
                request.getTreatmentCategory(),
                request.getPurposeType()
        );

        CalculationResult result = calculateByRule(request.getMedicalCost(), coverageRule);
        return CalculationResponse.from(result);
    }

    /**
     * 보장 룰 기반 계산 수행
     */
    private CalculationResult calculateByRule(Integer medicalCost, CoverageRule coverageRule) {
        if (!coverageRule.getIsCovered()) {
            return CalculationResult.of(
                    false,
                    0,
                    medicalCost,
                    List.of(coverageRule.getBasis()),
                    coverageRule.getDisclaimer()
            );
        }

        int refundAmountByRate = medicalCost * coverageRule.getCoverageRate() / 100;
        int limitedRefundAmount = applyLimit(refundAmountByRate, coverageRule.getLimitAmount());
        int deductibleAmount = Math.max(
                coverageRule.getDeductibleAmount(),
                medicalCost - limitedRefundAmount
        );
        int refundAmount = Math.max(medicalCost - deductibleAmount, 0);

        return CalculationResult.of(
                true,
                refundAmount,
                deductibleAmount,
                List.of(coverageRule.getBasis()),
                coverageRule.getDisclaimer()
        );
    }

    /**
     * 보장 한도 적용
     */
    private int applyLimit(int refundAmount, Integer limitAmount) {
        if (limitAmount == null) {
            return refundAmount;
        }

        return Math.min(refundAmount, limitAmount);
    }
}
