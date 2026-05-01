package com.silsonfit.silsonfit_api.domain.analysis.dto;

import com.silsonfit.silsonfit_api.domain.analysis.vo.AnalysisResult;

// AI 분석결과와 메타데이터를 묶어 분석이력 엔티티를 생성할 때 넘겨주는 dto
public record AnalysisHistoryCreateCommand(
        Long userId,
        String originalFileName,
        String pdfFileUrl,
        String companyName,
        String productName,
        String contractType,
        String generation,
        String coverageStructure,
        String cautionPoint,
        AnalysisResult aiSummary
) {}
