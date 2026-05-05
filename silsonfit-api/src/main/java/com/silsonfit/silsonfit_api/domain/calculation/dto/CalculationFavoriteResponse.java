package com.silsonfit.silsonfit_api.domain.calculation.dto;

import com.silsonfit.silsonfit_api.domain.calculation.entity.CalculationHistory;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * 계산 이력 즐겨찾기 응답
 */
public record CalculationFavoriteResponse(
        String id,
        OffsetDateTime calculatedDate,
        String insuranceCoverage,
        Integer medicalCost,
        Integer refundAmount,
        Boolean isCovered,
        Boolean isFavorite
) {

    public static CalculationFavoriteResponse from(CalculationHistory history) {
        return new CalculationFavoriteResponse(
                "calc_" + history.getId(),
                history.getCreatedAt().atOffset(ZoneOffset.UTC),
                history.getTreatmentCategory().getDescription(),
                history.getMedicalCost(),
                history.getRefundAmount(),
                history.getIsCovered(),
                history.getIsFavorite()
        );
    }
}
