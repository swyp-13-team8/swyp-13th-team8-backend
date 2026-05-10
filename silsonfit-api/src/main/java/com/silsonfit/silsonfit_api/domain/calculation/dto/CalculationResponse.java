package com.silsonfit.silsonfit_api.domain.calculation.dto;

import com.silsonfit.silsonfit_api.domain.calculation.entity.CoverageRule;
import com.silsonfit.silsonfit_api.domain.calculation.enums.CoverageStatus;
import com.silsonfit.silsonfit_api.domain.calculation.vo.CalculationResult;
import com.silsonfit.silsonfit_api.domain.insurance.dto.InsuranceInfoDto;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 실손 보험 계산 응답 DTO
 */
@Getter
@Builder
public class CalculationResponse {

    private static final String DEFAULT_DISCLAIMER =
            "본 계산 결과는 입력하신 정보를 바탕으로 AI가 산출한 참고용 결과입니다. "
                    + "실제 금액은 가입자 개인의 조건에 따라 차이가 발생하거나 지급이 제한될 수 있습니다. "
                    + "정확한 금액은 보험사를 통해 확인하시기 바랍니다";

    /** 보장 여부 */
    private CoverageStatus isCovered;

    /** 예상 환급액 */
    private Integer refundAmount;

    /** 총 자기부담금 */
    private Integer deductibleAmount;

    /** 계산 근거 */
    private String basis;

    /** 공제 기준 */
    private String deductibleBasis;

    /** 정액 공제 금액 */
    private Integer fixedDeductibleAmount;

    /** 정액 공제 금액 비율 */
    private Integer fixedDeductibleRate;

    /** 면책/주의사항 */
    private String disclaimer;

    /** 진료 정보 */
    private List<String> treatmentInfos;

    /** 총 진료비 */
    private Integer totalMedicalCost;

    /** 보험 이름 */
    private String productName;

    /** 보험 회사 */
    private String companyName;

    /** 보험 정보 */
    private List<String> insuranceInfos;

    /** 가입 날짜 */
    private String joinDate;

    /** 자기부담금 비율 */
    private Integer deductibleRate;

    /** 예상 환급 비율 */
    private Integer refundRate;

    /** EDI 코드 */
    private String ediCode;

    /** VO → DTO 변환 */
    public static CalculationResponse from(
            CalculationRequest request,
            CoverageRule coverageRule,
            CalculationResult result,
            InsuranceInfoDto insuranceInfo
    ) {
        return CalculationResponse.builder()
                .isCovered(toCoverageStatus(result))
                .refundAmount(result.getRefundAmount())
                .deductibleAmount(result.getDeductibleAmount())
                .basis(joinBasis(result.getBasis()))
                .deductibleBasis(createDeductibleBasis(coverageRule, result))
                .fixedDeductibleAmount(createFixedDeductibleAmount(request, coverageRule, result))
                .fixedDeductibleRate(calculateRate(
                        createFixedDeductibleAmount(request, coverageRule, result),
                        request.getMedicalCost()
                ))
                .disclaimer(resolveDisclaimer(result.getDisclaimer()))
                .treatmentInfos(createTreatmentInfos(request))
                .totalMedicalCost(request.getMedicalCost())
                .productName(insuranceInfo.productName())
                .companyName(insuranceInfo.companyName())
                .insuranceInfos(createInsuranceInfos(insuranceInfo))
                .joinDate(insuranceInfo.subscribedAt())
                .deductibleRate(calculateRate(result.getDeductibleAmount(), request.getMedicalCost()))
                .refundRate(calculateRate(result.getRefundAmount(), request.getMedicalCost()))
                .ediCode(request.getEdiCode())
                .build();
    }

    private static CoverageStatus toCoverageStatus(CalculationResult result) {
        if (!result.getIsCovered()) {
            return CoverageStatus.NOT_COVERED;
        }

        if (result.getDeductibleAmount() > 0) {
            return CoverageStatus.PARTIAL_COVERED;
        }

        return CoverageStatus.COVERED;
    }

    private static String joinBasis(List<String> basis) {
        if (basis == null || basis.isEmpty()) {
            return "";
        }

        return String.join(" ", basis);
    }

    private static String createDeductibleBasis(CoverageRule coverageRule, CalculationResult result) {
        if (!result.getIsCovered()) {
            return "보장 제외로 진료비 전액 자기부담";
        }

        int deductibleRate = 100 - coverageRule.getCoverageRate();
        return String.format(
                "%,d원 공제 후 잔여 진료비의 %d%% 보장(잔여 자기부담 %d%%)",
                coverageRule.getDeductibleAmount(),
                coverageRule.getCoverageRate(),
                deductibleRate
        );
    }

    private static Integer createFixedDeductibleAmount(
            CalculationRequest request,
            CoverageRule coverageRule,
            CalculationResult result
    ) {
        if (!result.getIsCovered()) {
            return 0;
        }

        return Math.min(request.getMedicalCost(), coverageRule.getDeductibleAmount());
    }

    private static String resolveDisclaimer(String disclaimer) {
        if (disclaimer == null || disclaimer.isBlank()) {
            return DEFAULT_DISCLAIMER;
        }

        return disclaimer;
    }

    private static List<String> createTreatmentInfos(CalculationRequest request) {
        return Stream.of(
                        request.getVisitType().getDescription(),
                        request.getTreatmentCategory().getDescription(),
                        request.getPayType().getDescription()
                )
                .filter(Objects::nonNull)
                .toList();
    }

    private static List<String> createInsuranceInfos(InsuranceInfoDto insuranceInfo) {
        List<String> insuranceInfos = new ArrayList<>();
        insuranceInfos.add(insuranceInfo.generation() + "세대");

        if (insuranceInfo.coverageStructure() != null) {
            insuranceInfos.add(insuranceInfo.coverageStructure().getDisplayName());
        }

        if (insuranceInfo.hasNonCoveredRider()) {
            insuranceInfos.add("비급여특약");
        }

        return insuranceInfos.stream()
                .filter(info -> info != null && !info.isBlank())
                .collect(Collectors.toList());
    }

    private static Integer calculateRate(Integer amount, Integer totalAmount) {
        if (totalAmount == null || totalAmount == 0) {
            return 0;
        }

        return Math.round(amount * 100.0f / totalAmount);
    }
}
