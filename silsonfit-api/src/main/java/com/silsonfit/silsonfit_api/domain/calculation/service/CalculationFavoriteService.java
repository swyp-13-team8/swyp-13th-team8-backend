package com.silsonfit.silsonfit_api.domain.calculation.service;

import com.silsonfit.silsonfit_api.domain.calculation.dto.CalculationFavoriteResponse;
import com.silsonfit.silsonfit_api.domain.calculation.entity.CalculationHistory;
import com.silsonfit.silsonfit_api.domain.calculation.repository.CalculationHistoryRepository;
import com.silsonfit.silsonfit_api.domain.insurance.dto.InsuranceInfoDto;
import com.silsonfit.silsonfit_api.domain.insurance.service.InsuranceService;
import com.silsonfit.silsonfit_api.global.error.BusinessException;
import com.silsonfit.silsonfit_api.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 계산 이력 즐겨찾기 Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalculationFavoriteService {

    private final CalculationHistoryRepository calculationHistoryRepository;
    private final InsuranceService insuranceService;

    /**
     * 계산 이력 즐겨찾기 목록 조회
     */
    public List<CalculationFavoriteResponse> getFavorites(Long userId) {
        return calculationHistoryRepository.findFavoritesByUserId(userId).stream()
                .map(history -> CalculationFavoriteResponse.from(history, resolveInsuranceInfo(history.getInsuranceId())))
                .toList();
    }

    private InsuranceInfoDto resolveInsuranceInfo(Long userInsuranceId) {
        try {
            return insuranceService.getInsuranceInfo(userInsuranceId);
        } catch (BusinessException e) {
            log.warn("계산 즐겨찾기 이력의 보험 정보를 찾을 수 없습니다. - userInsuranceId={}", userInsuranceId);
            return null;
        }
    }

    /**
     * 계산 이력 즐겨찾기 토글
     */
    @Transactional
    public void toggleFavorite(Long userId, Long calculationHistoryId) {
        CalculationHistory history = getOwnedHistory(userId, calculationHistoryId);
        history.toggleFavorite();
    }

    private CalculationHistory getOwnedHistory(Long userId, Long calculationHistoryId) {
        CalculationHistory history = calculationHistoryRepository.findById(calculationHistoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CALCULATION_HISTORY_NOT_FOUND));

        if (!history.getUserId().equals(userId)) {
            log.warn("계산 이력 접근 권한이 없습니다. - userId={}, calculationHistoryId={}", userId, calculationHistoryId);
            throw new BusinessException(ErrorCode.CALCULATION_HISTORY_ACCESS_DENIED);
        }

        return history;
    }
}
