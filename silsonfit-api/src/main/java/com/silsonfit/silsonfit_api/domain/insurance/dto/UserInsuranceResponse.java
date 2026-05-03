package com.silsonfit.silsonfit_api.domain.insurance.dto;

/**
 * 사용자 등록 보험 목록 응답 DTO
 */
public record UserInsuranceResponse(
        Long id,
        String companyName,
        String productName,
        int generation,
        String joinDate
) {
}
