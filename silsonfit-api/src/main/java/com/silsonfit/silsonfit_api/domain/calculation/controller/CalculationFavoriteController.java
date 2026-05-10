package com.silsonfit.silsonfit_api.domain.calculation.controller;

import com.silsonfit.silsonfit_api.domain.calculation.dto.CalculationFavoriteResponse;
import com.silsonfit.silsonfit_api.domain.calculation.service.CalculationFavoriteService;
import com.silsonfit.silsonfit_api.global.auth.CustomUserDetails;
import com.silsonfit.silsonfit_api.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 계산 이력 즐겨찾기 API
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/calculations")
public class CalculationFavoriteController {

    private final CalculationFavoriteService calculationFavoriteService;

    /**
     * 계산 이력 즐겨찾기 목록 조회
     */
    @GetMapping({"/favorites", "/save"})
    public ApiResponse<List<CalculationFavoriteResponse>> getFavorites(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();

        log.info("계산 이력 즐겨찾기 목록 조회 요청 - userId={}", userId);

        return ApiResponse.success(calculationFavoriteService.getFavorites(userId));
    }

    /**
     * 계산 이력 즐겨찾기 토글
     */
    @PatchMapping("/{calculationHistoryId}/favorite")
    public ApiResponse<Void> toggleFavorite(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long calculationHistoryId
    ) {
        Long userId = userDetails.getUserId();

        log.info("계산 이력 즐겨찾기 토글 요청 - userId={}, calculationHistoryId={}", userId, calculationHistoryId);

        calculationFavoriteService.toggleFavorite(userId, calculationHistoryId);

        return ApiResponse.success();
    }
}
