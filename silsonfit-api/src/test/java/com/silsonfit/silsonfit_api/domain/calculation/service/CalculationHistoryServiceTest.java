package com.silsonfit.silsonfit_api.domain.calculation.service;

import com.silsonfit.silsonfit_api.domain.calculation.dto.CalculationHistoryListResponse;
import com.silsonfit.silsonfit_api.domain.calculation.entity.CalculationHistory;
import com.silsonfit.silsonfit_api.domain.calculation.enums.TreatmentCategory;
import com.silsonfit.silsonfit_api.domain.calculation.repository.CalculationHistoryRepository;
import com.silsonfit.silsonfit_api.global.error.BusinessException;
import com.silsonfit.silsonfit_api.global.error.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class CalculationHistoryServiceTest {

    @Autowired
    CalculationHistoryService calculationHistoryService;

    @Autowired
    CalculationHistoryRepository calculationHistoryRepository;

    @Test
    void 사용자_계산_이력만_최신순으로_조회한다() {
        CalculationHistory firstHistory = calculationHistoryRepository.save(createHistory(
                1L,
                TreatmentCategory.MRI,
                300000,
                250000,
                true
        ));
        CalculationHistory otherUserHistory = calculationHistoryRepository.save(createHistory(
                2L,
                TreatmentCategory.CT,
                200000,
                100000,
                true
        ));
        CalculationHistory secondHistory = calculationHistoryRepository.save(createHistory(
                1L,
                TreatmentCategory.GENERAL,
                100000,
                70000,
                true
        ));
        CalculationHistory deletedHistory = calculationHistoryRepository.save(createHistory(
                1L,
                TreatmentCategory.CT,
                500000,
                300000,
                true
        ));
        deletedHistory.delete();
        calculationHistoryRepository.flush();

        CalculationHistoryListResponse response = calculationHistoryService.getHistories(
                1L,
                PageRequest.of(0, 20)
        );

        assertThat(response.calculations()).hasSize(2);
        assertThat(response.calculations().get(0).id()).isEqualTo(secondHistory.getId());
        assertThat(response.calculations().get(0).calculationHistoryId()).isEqualTo(String.valueOf(secondHistory.getId()));
        assertThat(response.calculations().get(0).insuranceCoverage()).isEqualTo("일반진료");
        assertThat(response.calculations().get(0).medicalCost()).isEqualTo(100000);
        assertThat(response.calculations().get(0).refundAmount()).isEqualTo(70000);
        assertThat(response.calculations().get(0).isCovered()).isTrue();
        assertThat(response.calculations().get(0).isSaved()).isFalse();
        assertThat(response.calculations().get(1).id()).isEqualTo(firstHistory.getId());
        assertThat(response.calculations())
                .noneSatisfy(calculation -> assertThat(calculation.id()).isEqualTo(otherUserHistory.getId()));
        assertThat(response.calculations())
                .noneSatisfy(calculation -> assertThat(calculation.id()).isEqualTo(deletedHistory.getId()));
        assertThat(response.pageInfo().page()).isZero();
        assertThat(response.pageInfo().size()).isEqualTo(20);
        assertThat(response.pageInfo().totalElements()).isEqualTo(2);
    }

    @Test
    void 사용자_계산_이력을_soft_delete한다() {
        CalculationHistory history = calculationHistoryRepository.save(createHistory(
                1L,
                TreatmentCategory.MRI,
                300000,
                250000,
                true
        ));
        history.toggleFavorite();
        calculationHistoryRepository.flush();

        calculationHistoryService.deleteHistory(1L, history.getId());

        assertThat(history.getIsDeleted()).isTrue();
        assertThat(history.getIsFavorite()).isFalse();
        assertThat(history.getDeletedAt()).isNotNull();
    }

    @Test
    void 다른_사용자의_계산_이력은_삭제할_수_없다() {
        CalculationHistory history = calculationHistoryRepository.save(createHistory(
                2L,
                TreatmentCategory.MRI,
                300000,
                250000,
                true
        ));
        calculationHistoryRepository.flush();

        assertThatThrownBy(() -> calculationHistoryService.deleteHistory(1L, history.getId()))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.CALCULATION_HISTORY_ACCESS_DENIED);
    }

    private CalculationHistory createHistory(
            Long userId,
            TreatmentCategory treatmentCategory,
            Integer medicalCost,
            Integer refundAmount,
            Boolean isCovered
    ) {
        return CalculationHistory.create(
                userId,
                1L,
                medicalCost,
                treatmentCategory,
                null,
                isCovered,
                refundAmount,
                medicalCost - refundAmount
        );
    }
}
