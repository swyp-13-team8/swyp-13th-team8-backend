package com.silsonfit.silsonfit_api.domain.insurance.dto;

/**
 * 보험 상품 목록 응답 DTO
 */
public record InsuranceProductResponse(
        Long id,
        String productName
) {
}
