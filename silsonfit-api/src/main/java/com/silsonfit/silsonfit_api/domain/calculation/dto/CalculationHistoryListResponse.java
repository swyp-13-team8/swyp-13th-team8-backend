package com.silsonfit.silsonfit_api.domain.calculation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.silsonfit.silsonfit_api.domain.calculation.entity.CalculationHistory;
import com.silsonfit.silsonfit_api.domain.insurance.dto.InsuranceInfoDto;
import org.springframework.data.domain.Page;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.function.Function;

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

    public static CalculationHistoryListResponse from(
            Page<CalculationHistory> histories,
            Function<Long, InsuranceInfoDto> insuranceInfoResolver
    ) {
        return new CalculationHistoryListResponse(
                histories.getContent().stream()
                        .map(history -> CalculationSummary.from(history, insuranceInfoResolver.apply(history.getInsuranceId())))
                        .toList(),
                PageInfo.from(histories)
        );
    }

    @JsonProperty("content")
    public List<CalculationSummary> content() {
        return calculations;
    }

    public record CalculationSummary(
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
            Boolean isSaved
    ) {

        public static CalculationSummary from(CalculationHistory history) {
            return from(history, null);
        }

        public static CalculationSummary from(CalculationHistory history, InsuranceInfoDto insuranceInfo) {
            return new CalculationSummary(
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
                    history.getIsFavorite()
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
