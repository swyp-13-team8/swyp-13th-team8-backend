package com.silsonfit.silsonfit_api.domain.analysis.dto;

import org.springframework.web.multipart.MultipartFile;

public record AnalysisRequest(
        String clientId,        // 프론트에서 넘겨주는 아이디 (sse Map 의 키 값)
        Long userInsuranceId,   // 내 보험 아이디 ( 보험을 선택하지 않으면 null )
        MultipartFile file      // pdf 파일 ( 보험을 선택했다면 null )
) {
    public boolean isValid() {
        return userInsuranceId != null || (file != null && !file.isEmpty());
    }
}
