package com.silsonfit.silsonfit_api.domain.calculation.service;

import com.silsonfit.silsonfit_api.domain.calculation.dto.CalculationHistoryListResponse;
import com.silsonfit.silsonfit_api.domain.calculation.entity.CalculationHistory;
import com.silsonfit.silsonfit_api.domain.calculation.enums.TreatmentCategory;
import com.silsonfit.silsonfit_api.domain.calculation.repository.CalculationHistoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

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
                TreatmentCategory.GENERAL_TREATMENT,
                100000,
                70000,
                true
        ));
        calculationHistoryRepository.flush();

        CalculationHistoryListResponse response = calculationHistoryService.getHistories(
                1L,
                PageRequest.of(0, 20)
        );

        assertThat(response.calculations()).hasSize(2);
        assertThat(response.calculations().get(0).id()).isEqualTo("calc_" + secondHistory.getId());
        assertThat(response.calculations().get(0).insuranceCoverage()).isEqualTo("일반진료");
        assertThat(response.calculations().get(0).medicalCost()).isEqualTo(100000);
        assertThat(response.calculations().get(0).refundAmount()).isEqualTo(70000);
        assertThat(response.calculations().get(0).isCovered()).isTrue();
        assertThat(response.calculations().get(1).id()).isEqualTo("calc_" + firstHistory.getId());
        assertThat(response.calculations())
                .noneSatisfy(calculation -> assertThat(calculation.id()).isEqualTo("calc_" + otherUserHistory.getId()));
        assertThat(response.pageInfo().page()).isZero();
        assertThat(response.pageInfo().size()).isEqualTo(20);
        assertThat(response.pageInfo().totalElements()).isEqualTo(2);
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
