package com.silsonfit.silsonfit_api.domain.insurance.dto;

/**
 * 보험 상품 목록 응답 DTO
 *
 * @param id 보험 상품 ID
 * @param productName 상품명
 * @param contractType 계약 유형 라벨 (예: "개인실손")
 * @param generation 보험 세대
 * @param coverageStructure 보장 구조 라벨 (예: "3대비급여")
 * @param cautionPoint 주의 포인트 라벨 (예: "갱신형")
 */
public record InsuranceProductResponse(
        Long id,
        String productName,
        String contractType,
        int generation,
        String coverageStructure,
        String cautionPoint
) {
}
