package com.silsonfit.silsonfit_api.domain.analysis.dto;

import java.util.List;

/**
 * AI API 의 응답을 받을 수 있는 DTO
 */
public record AnalysisResultDTO(
        Metadata metadata,
        SummaryContent content
) {

    /**
     * PDF 표지 / 개요 등에서 추출한 보험 기본 정보 ( 보험이 없다면 이 정보를 사용함 )
     */
    public record Metadata(
            String companyName,         // 보험사
            String productName,         // 약관 이름
            String contractType,        // 계약 유형
            String generation,          // 세대
            String coverageStructure,   // 보장 구조
            String cautionPoint         // 주의 포인트
    ) {}

    /**
     * 보험 상세 분석 결과
     */
    public record SummaryContent(
            List<String> coreSummary,           // AI 핵심 요약
            CoverageDetails coverageDetails,    // 보장 구조
            List<String> coverageScope,         // 보장 범위
            List<String> limitations,           // 제한 조건
            List<String> renewalTerms,          // 갱신, 재가입
            List<String> claimMethod,           // 청구 방법
            List<String> cancellationAndRefund  // 해지, 환급
    ) {}

    public record CoverageDetails(
            List<String> basicCoverages,    // 기본 보장
            List<String> specialCoverages,  // 추가 보장 (특약 가입 시)
            List<String> exclusions         // 보장되지 않는 항목
    ) {
    }
}
