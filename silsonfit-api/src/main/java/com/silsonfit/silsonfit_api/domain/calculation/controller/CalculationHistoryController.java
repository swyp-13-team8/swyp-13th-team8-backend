package com.silsonfit.silsonfit_api.domain.calculation.controller;

import com.silsonfit.silsonfit_api.domain.calculation.dto.CalculationHistoryListResponse;
import com.silsonfit.silsonfit_api.domain.calculation.service.CalculationHistoryService;
import com.silsonfit.silsonfit_api.global.auth.CustomUserDetails;
import com.silsonfit.silsonfit_api.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 실손 보험 계산 이력 API
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/calculations")
public class CalculationHistoryController {

    private final CalculationHistoryService calculationHistoryService;

    /**
     * 실손 보험 계산 이력 목록 조회
     */
    @GetMapping
    public ApiResponse<CalculationHistoryListResponse> getHistories(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Long userId = userDetails.getUserId();

        log.info("실손 보험 계산 이력 목록 조회 요청 - userId={}, page={}, size={}",
                userId,
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        return ApiResponse.success(calculationHistoryService.getHistories(userId, pageable));
    }
}
