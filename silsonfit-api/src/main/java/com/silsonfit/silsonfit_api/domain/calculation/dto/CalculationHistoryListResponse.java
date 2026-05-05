package com.silsonfit.silsonfit_api.domain.calculation.dto;

import com.silsonfit.silsonfit_api.domain.calculation.entity.CalculationHistory;
import org.springframework.data.domain.Page;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * 계산 이력 목록 응답
 */
public record CalculationHistoryListResponse(
        List<CalculationSummary> calculations,
        PageInfo pageInfo
) {

    public static CalculationHistoryListResponse from(Page<CalculationHistory> histories) {
        return new CalculationHistoryListResponse(
                histories.getContent().stream()
                        .map(CalculationSummary::from)
                        .toList(),
                PageInfo.from(histories)
        );
    }

    public record CalculationSummary(
            String id,
            OffsetDateTime calculatedDate,
            String insuranceCoverage,
            Integer medicalCost,
            Integer refundAmount,
            Boolean isCovered
    ) {

        public static CalculationSummary from(CalculationHistory history) {
            return new CalculationSummary(
                    "calc_" + history.getId(),
                    history.getCreatedAt().atOffset(ZoneOffset.UTC),
                    history.getTreatmentCategory().getDescription(),
                    history.getMedicalCost(),
                    history.getRefundAmount(),
                    history.getIsCovered()
            );
        }
    }

    public record PageInfo(
            int page,
            int size,
            int totalPages,
            long totalElements
    ) {

        public static PageInfo from(Page<?> page) {
            return new PageInfo(
                    page.getNumber(),
                    page.getSize(),
                    page.getTotalPages(),
                    page.getTotalElements()
            );
        }
    }
}
