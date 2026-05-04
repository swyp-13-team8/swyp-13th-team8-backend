package com.silsonfit.silsonfit_api.domain.insurance.dto;

/**
 * 사용자 등록 보험 목록 응답 DTO
 *
 * @param userInsuranceId 보험 등록 건의 고유 번호
 * @param companyName 보험사명
 * @param productName 보험 상품명
 * @param generation 보험 세대
 * @param joinDate 가입 연월
 */
public record UserInsuranceResponse(
        Long userInsuranceId,
        String companyName,
        String productName,
        int generation,
        String joinDate
) {
}
