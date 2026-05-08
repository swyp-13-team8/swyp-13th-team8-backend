package com.silsonfit.silsonfit_api.domain.calculation.service;

import com.silsonfit.silsonfit_api.domain.calculation.dto.CalculationFavoriteResponse;
import com.silsonfit.silsonfit_api.domain.calculation.entity.CalculationHistory;
import com.silsonfit.silsonfit_api.domain.calculation.enums.TreatmentCategory;
import com.silsonfit.silsonfit_api.domain.calculation.repository.CalculationHistoryRepository;
import com.silsonfit.silsonfit_api.domain.insurance.service.InsuranceService;
import com.silsonfit.silsonfit_api.global.error.BusinessException;
import com.silsonfit.silsonfit_api.global.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CalculationFavoriteServiceTest {

    @Mock
    CalculationHistoryRepository calculationHistoryRepository;

    @Mock
    InsuranceService insuranceService;

    @InjectMocks
    CalculationFavoriteService calculationFavoriteService;

    @Test
    @DisplayName("계산 이력 즐겨찾기를 토글하여 등록한다")
    void toggleFavorite_markFavorite() {
        CalculationHistory history = createHistory(1L, 10L);
        given(calculationHistoryRepository.findById(1L)).willReturn(Optional.of(history));

        calculationFavoriteService.toggleFavorite(10L, 1L);

        assertThat(history.getIsFavorite()).isTrue();
    }

    @Test
    @DisplayName("다른 사용자의 계산 이력은 즐겨찾기 토글할 수 없다")
    void toggleFavorite_accessDenied() {
        CalculationHistory history = createHistory(1L, 99L);
        given(calculationHistoryRepository.findById(1L)).willReturn(Optional.of(history));

        assertThatThrownBy(() -> calculationFavoriteService.toggleFavorite(10L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.CALCULATION_HISTORY_ACCESS_DENIED);
    }

    @Test
    @DisplayName("즐겨찾기한 계산 이력 목록을 조회한다")
    void getFavorites_success() {
        CalculationHistory history = createHistory(1L, 10L);
        history.toggleFavorite();
        given(calculationHistoryRepository.findFavoritesByUserId(10L)).willReturn(List.of(history));

        List<CalculationFavoriteResponse> responses = calculationFavoriteService.getFavorites(10L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).id()).isEqualTo(1L);
        assertThat(responses.get(0).calculationHistoryId()).isEqualTo("1");
        assertThat(responses.get(0).insuranceCoverage()).isEqualTo("MRI");
        assertThat(responses.get(0).medicalCost()).isEqualTo(100000);
        assertThat(responses.get(0).refundAmount()).isEqualTo(70000);
        assertThat(responses.get(0).isCovered()).isTrue();
        assertThat(responses.get(0).isFavorite()).isTrue();
        assertThat(responses.get(0).isSaved()).isTrue();
    }

    @Test
    @DisplayName("계산 이력 즐겨찾기를 토글하여 해제한다")
    void toggleFavorite_unmarkFavorite() {
        CalculationHistory history = createHistory(1L, 10L);
        history.toggleFavorite();
        given(calculationHistoryRepository.findById(1L)).willReturn(Optional.of(history));

        calculationFavoriteService.toggleFavorite(10L, 1L);

        assertThat(history.getIsFavorite()).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 계산 이력은 예외가 발생한다")
    void favorite_historyNotFound() {
        given(calculationHistoryRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> calculationFavoriteService.toggleFavorite(10L, 999L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.CALCULATION_HISTORY_NOT_FOUND);
    }

    private CalculationHistory createHistory(Long historyId, Long userId) {
        CalculationHistory history = CalculationHistory.create(
                userId,
                1L,
                100000,
                TreatmentCategory.MRI,
                "MRI001",
                true,
                70000,
                30000
        );
        ReflectionTestUtils.setField(history, "id", historyId);
        ReflectionTestUtils.setField(history, "createdAt", LocalDateTime.of(2026, 5, 4, 10, 0));
        return history;
    }
}
