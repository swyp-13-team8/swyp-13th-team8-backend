package com.silsonfit.silsonfit_api.domain.analysis.dto;

import com.silsonfit.silsonfit_api.domain.analysis.entity.AnalysisHistory;
import com.silsonfit.silsonfit_api.domain.analysis.vo.AnalysisResult;

public record AnalysisHistoryDetailResponse(
        Long analysisHistoryId,
        String originalFileName,
        String pdfFileUrl,
        String companyName,
        String productName,
        String contractType,
        String generation,
        String coverageStructure,
        String cautionPoint,
        AnalysisResult aiSummary
) {
    // 1. 회원 이력 단건 조회용
    public static AnalysisHistoryDetailResponse from(AnalysisHistory history) {
        return new AnalysisHistoryDetailResponse(
                history.getId(),
                history.getOriginalFileName(),
                history.getPdfFileUrl(),
                history.getCompanyName(),
                history.getProductName(),
                history.getContractType(),
                history.getGeneration(),
                history.getCoverageStructure(),
                history.getCautionPoint(),
                history.getAiSummary()
        );
    }

    // 2. 분석 직후 결과 반환용
    public static AnalysisHistoryDetailResponse of(AnalysisResult result, String originalFileName,
                                                                   String pdfFileUrl) {
        return new AnalysisHistoryDetailResponse(
                null,
                originalFileName,
                pdfFileUrl,
                result.metadata().companyName(),
                result.metadata().productName(),
                result.metadata().contractType(),
                result.metadata().generation(),
                result.metadata().coverageStructure(),
                result.metadata().cautionPoint(),
                result
        );
    }
}
