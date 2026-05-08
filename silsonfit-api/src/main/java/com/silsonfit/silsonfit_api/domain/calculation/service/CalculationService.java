package com.silsonfit.silsonfit_api.domain.calculation.service;

import com.silsonfit.silsonfit_api.domain.calculation.dto.CalculationRequest;
import com.silsonfit.silsonfit_api.domain.calculation.dto.CalculationResponse;
import com.silsonfit.silsonfit_api.domain.calculation.entity.CalculationHistory;
import com.silsonfit.silsonfit_api.domain.calculation.entity.CoverageRule;
import com.silsonfit.silsonfit_api.domain.calculation.enums.InsuranceGeneration;
import com.silsonfit.silsonfit_api.domain.calculation.repository.CalculationHistoryRepository;
import com.silsonfit.silsonfit_api.domain.calculation.vo.CalculationResult;
import com.silsonfit.silsonfit_api.domain.calculation.vo.CoverageRuleContext;
import com.silsonfit.silsonfit_api.domain.insurance.dto.InsuranceInfoDto;
import com.silsonfit.silsonfit_api.domain.insurance.service.InsuranceService;
import com.silsonfit.silsonfit_api.global.error.BusinessException;
import com.silsonfit.silsonfit_api.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final CalculationHistoryRepository calculationHistoryRepository;
    private final InsuranceService insuranceService;

    /**
     * 실손 보험 예상 환급액 계산
     *
     * @param userId 사용자 ID
     * @param request 계산 요청
     * @return 계산 응답
     */
    @Transactional
    public CalculationResponse calculate(Long userId, CalculationRequest request) {
        Long userInsuranceId = parseUserInsuranceId(request.getInsuranceId());
        InsuranceInfoDto insuranceInfo = insuranceService.getInsuranceInfo(userInsuranceId);

        CoverageRuleContext context = new CoverageRuleContext(
                insuranceInfo.insuranceId(),
                InsuranceGeneration.from(insuranceInfo.generation())
        );

        CoverageRule coverageRule = coverageRuleResolver.resolve(
                context,
                request.getEdiCode(),
                request.getVisitType(),
                request.getTreatmentCategory(),
                request.getPurposeType()
        );

        CalculationResult result = calculateByRule(request.getMedicalCost(), coverageRule);
        saveHistory(userId, userInsuranceId, request, result);
        return CalculationResponse.from(request, coverageRule, result, insuranceInfo);
    }

    /**
     * 계산 결과 이력 저장
     */
    private void saveHistory(
            Long userId,
            Long userInsuranceId,
            CalculationRequest request,
            CalculationResult result
    ) {
        CalculationHistory history = CalculationHistory.create(
                userId,
                userInsuranceId,
                request.getMedicalCost(),
                request.getTreatmentCategory(),
                request.getEdiCode(),
                result.getIsCovered(),
                result.getRefundAmount(),
                result.getDeductibleAmount()
        );

        calculationHistoryRepository.save(history);
    }

    /**
     * 클라이언트 표시용 보험 ID(ins_123)와 숫자 ID를 모두 사용자 보험 등록 ID로 해석한다.
     */
    private Long parseUserInsuranceId(String insuranceId) {
        if (insuranceId == null || insuranceId.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }

        String normalizedInsuranceId = insuranceId.trim();
        if (normalizedInsuranceId.startsWith("ins_")) {
            normalizedInsuranceId = normalizedInsuranceId.substring("ins_".length());
        }

        try {
            return Long.parseLong(normalizedInsuranceId);
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
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
                    coverageRule.getBasis(),
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
                coverageRule.getBasis(),
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
