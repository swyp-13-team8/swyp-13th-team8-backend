package com.silsonfit.silsonfit_api.domain.analysis.service;

import com.silsonfit.silsonfit_api.domain.analysis.dto.AnalysisHistoryDetailResponse;
import com.silsonfit.silsonfit_api.domain.analysis.dto.AnalysisHistoryListResponse;
import com.silsonfit.silsonfit_api.domain.analysis.entity.AnalysisHistory;
import com.silsonfit.silsonfit_api.domain.analysis.repository.AnalysisHistoryRepository;
import com.silsonfit.silsonfit_api.global.error.BusinessException;
import com.silsonfit.silsonfit_api.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AnalysisHistoryService {

    private final AnalysisHistoryRepository analysisHistoryRepository;

    /**
     * 분석 이력 리스트 조회
     *
     * @param userId 조회할 사용자 ID
     * @param pageable 페이징 정보
     * @return 페이징 처리된 분석 이력 리스트
     */
    public Page<AnalysisHistoryListResponse> getHistories(Long userId, Pageable pageable) {
        return analysisHistoryRepository.findHistoriesByUserId(userId, pageable)
                .map(AnalysisHistoryListResponse::from);
    }

    /**
     * 분석 이력 단건 조회
     *
     * @param userId 요청한 사용자 ID
     * @param historyId 조회할 이력 ID
     * @return 분석 이력 상세정보
     */
    public AnalysisHistoryDetailResponse getHistoryDetail(Long userId, Long historyId) {
        AnalysisHistory history = analysisHistoryRepository.findById(historyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.HISTORY_NOT_FOUND));

        if (!history.getUserId().equals(userId)) {
            log.warn("이력 조회 권한이 없습니다. - userId={}, historyId={}", userId, historyId);
            throw new BusinessException(ErrorCode.HISTORY_ACCESS_DENIED);
        }

        return AnalysisHistoryDetailResponse.from(history);
    }

    /**
     * 분석 이력 삭제
     *
     * @param userId 요청한 사용자 ID
     * @param historyId 삭제할 이력 ID
     */
    @Transactional
    public void deleteHistory(Long userId, Long historyId) {
        AnalysisHistory history = analysisHistoryRepository.findById(historyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.HISTORY_NOT_FOUND));

        if (!history.getUserId().equals(userId)) {
            log.warn("이력 삭제 권한이 없습니다. - userId={}, historyId={}", userId, historyId);
            throw new BusinessException(ErrorCode.HISTORY_ACCESS_DENIED);
        }

        // TODO: S3 파일 삭제 로직 추가 예정

        analysisHistoryRepository.delete(history);
    }
}
