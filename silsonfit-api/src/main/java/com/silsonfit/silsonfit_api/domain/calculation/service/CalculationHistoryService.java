package com.silsonfit.silsonfit_api.domain.calculation.service;

import com.silsonfit.silsonfit_api.domain.calculation.dto.CalculationHistoryListResponse;
import com.silsonfit.silsonfit_api.domain.calculation.repository.CalculationHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 실손 보험 계산 이력 Service
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalculationHistoryService {

    private final CalculationHistoryRepository calculationHistoryRepository;

    /**
     * 사용자별 계산 이력 목록 조회
     */
    public CalculationHistoryListResponse getHistories(Long userId, Pageable pageable) {
        return CalculationHistoryListResponse.from(
                calculationHistoryRepository.findByUserIdOrderByCreatedAtDescIdDesc(userId, pageable)
        );
    }
}
