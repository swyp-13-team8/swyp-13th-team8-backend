package com.silsonfit.silsonfit_api.domain.calculation.dto;

import com.silsonfit.silsonfit_api.domain.calculation.entity.CalculationHistory;
import com.silsonfit.silsonfit_api.domain.insurance.dto.InsuranceInfoDto;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * 계산 이력 즐겨찾기 응답
 */
public record CalculationFavoriteResponse(
        Long id,
        String calculationHistoryId,
        OffsetDateTime calculatedDate,
        String insuranceCoverage,
        List<String> basis,
        String insuranceId,
        String productName,
        String companyName,
        String generation,
        String joinDate,
        String ediCode,
        Integer medicalCost,
        Integer refundAmount,
        Boolean isCovered,
        Boolean isFavorite,
        Boolean isSaved
) {

    public static CalculationFavoriteResponse from(CalculationHistory history) {
        return from(history, null);
    }

    public static CalculationFavoriteResponse from(CalculationHistory history, InsuranceInfoDto insuranceInfo) {
        return new CalculationFavoriteResponse(
                history.getId(),
                String.valueOf(history.getId()),
                history.getCreatedAt().atOffset(ZoneOffset.UTC),
                history.getTreatmentCategory().getDescription(),
                List.of(history.getTreatmentCategory().getDescription()),
                String.valueOf(history.getInsuranceId()),
                insuranceInfo != null ? insuranceInfo.productName() : null,
                insuranceInfo != null ? insuranceInfo.companyName() : null,
                insuranceInfo != null ? String.valueOf(insuranceInfo.generation()) : null,
                insuranceInfo != null ? insuranceInfo.subscribedAt() : null,
                history.getEdiCode(),
                history.getMedicalCost(),
                history.getRefundAmount(),
                history.getIsCovered(),
                history.getIsFavorite(),
                history.getIsFavorite()
        );
    }
}
