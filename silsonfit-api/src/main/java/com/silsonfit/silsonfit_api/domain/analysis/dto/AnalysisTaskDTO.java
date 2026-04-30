package com.silsonfit.silsonfit_api.domain.analysis.dto;

public record AnalysisTaskDTO(
        Long userId,
        String clientId,
        Long userInsuranceId,
        byte[] fileBytes,
        String contentType,
        String originalFileName
) {
}
