package com.silsonfit.silsonfit_api.domain.calculation.service;

import com.silsonfit.silsonfit_api.domain.calculation.dto.CalculationHistoryListResponse;
import com.silsonfit.silsonfit_api.domain.calculation.entity.CalculationHistory;
import com.silsonfit.silsonfit_api.domain.calculation.repository.CalculationHistoryRepository;
import com.silsonfit.silsonfit_api.domain.insurance.dto.InsuranceInfoDto;
import com.silsonfit.silsonfit_api.domain.insurance.service.InsuranceService;
import com.silsonfit.silsonfit_api.global.error.BusinessException;
import com.silsonfit.silsonfit_api.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 실손 보험 계산 이력 Service
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CalculationHistoryService {

    private final CalculationHistoryRepository calculationHistoryRepository;
    private final InsuranceService insuranceService;

    /**
     * 사용자별 계산 이력 목록 조회
     */
    public CalculationHistoryListResponse getHistories(Long userId, Pageable pageable) {
        Pageable fixedSortPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        return CalculationHistoryListResponse.from(
                calculationHistoryRepository.findByUserIdAndIsDeletedFalseOrderByCreatedAtDescIdDesc(userId, fixedSortPageable),
                this::resolveInsuranceInfo
        );
    }

    private InsuranceInfoDto resolveInsuranceInfo(Long userInsuranceId) {
        try {
            return insuranceService.getInsuranceInfo(userInsuranceId);
        } catch (BusinessException e) {
            log.warn("계산 이력의 보험 정보를 찾을 수 없습니다. - userInsuranceId={}", userInsuranceId);
            return null;
        }
    }

    /**
     * 사용자 계산 이력 삭제
     */
    @Transactional
    public void deleteHistory(Long userId, Long calculationHistoryId) {
        CalculationHistory history = calculationHistoryRepository.findById(calculationHistoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CALCULATION_HISTORY_NOT_FOUND));

        if (!history.getUserId().equals(userId)) {
            log.warn("계산 이력 삭제 권한이 없습니다. - userId={}, calculationHistoryId={}", userId, calculationHistoryId);
            throw new BusinessException(ErrorCode.CALCULATION_HISTORY_ACCESS_DENIED);
        }

        history.delete();
    }
}
