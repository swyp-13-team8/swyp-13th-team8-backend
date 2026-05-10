package com.silsonfit.silsonfit_api.domain.analysis.dto;

import com.silsonfit.silsonfit_api.domain.analysis.entity.AnalysisHistory;

import java.time.LocalDateTime;

public record AnalysisHistoryListResponse(
        Long analysisHistoryId,
        String companyName,         // 보험사
        String productName,         // 약관 이름
        String contractType,        // 계약 유형
        String generation,          // 보험 세대
        String coverageStructure,   // 보장 구조
        String cautionPoint,        // 주의 포인트
        Boolean isFavorite,         // 즐겨찾기 여부
        LocalDateTime createdAt     // 분석일
) {
    public static AnalysisHistoryListResponse from(AnalysisHistory history) {
        return new AnalysisHistoryListResponse(
                history.getId(),
                history.getCompanyName(),
                history.getProductName(),
                history.getContractType(),
                history.getGeneration(),
                history.getCoverageStructure(),
                history.getCautionPoint(),
                history.getIsFavorite(),
                history.getCreatedAt()
        );
    }
}
