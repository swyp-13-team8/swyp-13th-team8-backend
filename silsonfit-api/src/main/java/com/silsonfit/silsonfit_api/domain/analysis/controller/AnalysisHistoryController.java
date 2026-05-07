package com.silsonfit.silsonfit_api.domain.analysis.controller;

import com.silsonfit.silsonfit_api.domain.analysis.dto.AnalysisHistoryDetailResponse;
import com.silsonfit.silsonfit_api.domain.analysis.dto.AnalysisHistoryListResponse;
import com.silsonfit.silsonfit_api.domain.analysis.service.AnalysisHistoryService;
import com.silsonfit.silsonfit_api.global.auth.CustomUserDetails;
import com.silsonfit.silsonfit_api.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/history/analysis")
@Slf4j
public class AnalysisHistoryController {

    private final AnalysisHistoryService analysisHistoryService;

    /**
     * 분석 이력 리스트 조회
     *
     * @param userDetails 로그인한 사용자 정보
     * @param pageable 페이징 정보
     * @return 분석 이력 리스트
     */
    @GetMapping
    public ApiResponse<Page<AnalysisHistoryListResponse>> getHistories(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                       @PageableDefault(size = 5) Pageable pageable) {
        Long userId = userDetails.getUserId();

        log.info("분석 이력 리스트 조회 요청 - userId={}", userId);

        return ApiResponse.success(analysisHistoryService.getHistories(userId, pageable));
    }

    /**
     * 저장된 분석 이력 리스트 조회
     *
     * @param userDetails 로그인한 사용자 정보
     * @param pageable 페이징 정보
     * @return 저장된 분석 이력 리스트
     */
    @GetMapping("/favorite")
    public ApiResponse<Page<AnalysisHistoryListResponse>> getFavoriteHistories(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                               @PageableDefault(size = 5) Pageable pageable) {
        Long userId = userDetails.getUserId();

        log.info("저장(즐겨찾기)된 분석 이력 리스트 조회 요청 - userId={}", userId);

        return ApiResponse.success(analysisHistoryService.getFavoriteHistories(userId, pageable));
    }

    /**
     * 분석 이력 단건 조회
     *
     * @param userDetails 로그인한 사용자 정보
     * @param historyId 분석 이력 ID
     * @return 분석 이력 상세 정보
     */
    @GetMapping("/{historyId}")
    public ApiResponse<AnalysisHistoryDetailResponse> getHistoryDetail(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                       @PathVariable Long historyId) {
        Long userId = userDetails.getUserId();

        log.info("분석 이력 상세 조회 요청 - userId={}, historyId={}", userId, historyId);

        return ApiResponse.success(analysisHistoryService.getHistoryDetail(userId, historyId));

    }

    /**
     * 분석 이력 삭제
     *
     * @param userDetails 로그인한 사용자 정보
     * @param historyId 분석 이력 ID
     * @return 삭제 성공 시 빈 응답 (200)
     */
    @DeleteMapping("/{historyId}")
    public ApiResponse<Void> deleteHistory(@AuthenticationPrincipal CustomUserDetails userDetails,
                                        @PathVariable Long historyId) {
        Long userId = userDetails.getUserId();

        log.info("분석 이력 삭제 요청 - userId={}, historyId={}", userId, historyId);

        analysisHistoryService.deleteHistory(userId, historyId);

        log.info("분석 이력 삭제 성공 - userId={}, historyId={}", userId, historyId);

        return ApiResponse.success();
    }

    /**
     * 분석 이력 즐겨찾기 (토글방식)
     *
     * @param userDetails 로그인한 사용자 정보
     * @param historyId 분석 이력 ID
     * @return 삭제 성공 시 빈 응답 (200)
     */
    @PatchMapping("/{historyId}")
    public ApiResponse<Void> toggleFavorite(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @PathVariable Long historyId) {
        Long userId = userDetails.getUserId();

        log.info("이력 즐겨찾기 요청 - userId={}, historyId={}", userId, historyId);

        analysisHistoryService.toggleFavorite(userId, historyId);

        log.info("이력 즐겨찾기 상태 변경 완료 - userId={}, historyId={}", userId, historyId);

        return ApiResponse.success();
    }
}
