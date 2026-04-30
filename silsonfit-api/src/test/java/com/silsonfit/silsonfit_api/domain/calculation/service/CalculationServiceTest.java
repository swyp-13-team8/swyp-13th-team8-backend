package com.silsonfit.silsonfit_api.domain.calculation.service;

import com.silsonfit.silsonfit_api.domain.calculation.dto.CalculationRequest;
import com.silsonfit.silsonfit_api.domain.calculation.dto.CalculationResponse;
import com.silsonfit.silsonfit_api.domain.calculation.entity.CalculationHistory;
import com.silsonfit.silsonfit_api.domain.calculation.entity.CoverageRule;
import com.silsonfit.silsonfit_api.domain.calculation.enums.PurposeType;
import com.silsonfit.silsonfit_api.domain.calculation.enums.TreatmentCategory;
import com.silsonfit.silsonfit_api.domain.calculation.enums.VisitType;
import com.silsonfit.silsonfit_api.domain.calculation.repository.CalculationHistoryRepository;
import com.silsonfit.silsonfit_api.domain.calculation.repository.CoverageRuleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CalculationServiceTest {

    @Autowired
    CalculationService calculationService;

    @Autowired
    CoverageRuleRepository coverageRuleRepository;

    @Autowired
    CalculationHistoryRepository calculationHistoryRepository;

    @Test
    void 보장_대상이면_보장률에_따라_환급액과_자기부담금을_계산한다() {
        coverageRuleRepository.save(createCoverageRule(
                true,
                70,
                10000,
                null
        ));
        CalculationRequest request = createCalculationRequest(100000);

        CalculationResponse response = calculationService.calculate(1L, request);

        assertThat(response.getIsCovered()).isTrue();
        assertThat(response.getRefundAmount()).isEqualTo(70000);
        assertThat(response.getDeductibleAmount()).isEqualTo(30000);

        CalculationHistory history = calculationHistoryRepository.findAll().get(0);
        assertThat(history.getUserId()).isEqualTo(1L);
        assertThat(history.getInsuranceId()).isEqualTo(1L);
        assertThat(history.getMedicalCost()).isEqualTo(100000);
        assertThat(history.getTreatmentCategory()).isEqualTo(TreatmentCategory.MRI);
        assertThat(history.getRefundAmount()).isEqualTo(70000);
        assertThat(history.getDeductibleAmount()).isEqualTo(30000);
    }

    @Test
    void 고정_자기부담금이_더_크면_고정_자기부담금을_적용한다() {
        coverageRuleRepository.save(createCoverageRule(
                true,
                95,
                10000,
                null
        ));
        CalculationRequest request = createCalculationRequest(100000);

        CalculationResponse response = calculationService.calculate(1L, request);

        assertThat(response.getIsCovered()).isTrue();
        assertThat(response.getRefundAmount()).isEqualTo(90000);
        assertThat(response.getDeductibleAmount()).isEqualTo(10000);
    }

    @Test
    void 보장_제외이면_환급액은_0원이고_자기부담금은_의료비_전액이다() {
        coverageRuleRepository.save(createCoverageRule(
                false,
                0,
                0,
                null
        ));
        CalculationRequest request = createCalculationRequest(100000);

        CalculationResponse response = calculationService.calculate(1L, request);

        assertThat(response.getIsCovered()).isFalse();
        assertThat(response.getRefundAmount()).isZero();
        assertThat(response.getDeductibleAmount()).isEqualTo(100000);
    }

    private CoverageRule createCoverageRule(
            Boolean isCovered,
            Integer coverageRate,
            Integer deductibleAmount,
            Integer limitAmount
    ) {
        return CoverageRule.create(
                1L,
                null,
                VisitType.OUTPATIENT,
                TreatmentCategory.MRI,
                PurposeType.TREATMENT,
                isCovered,
                coverageRate,
                deductibleAmount,
                limitAmount,
                List.of("계산 테스트 룰"),
                null
        );
    }

    private CalculationRequest createCalculationRequest(Integer medicalCost) {
        CalculationRequest request = new CalculationRequest();
        ReflectionTestUtils.setField(request, "insuranceId", 1L);
        ReflectionTestUtils.setField(request, "medicalCost", medicalCost);
        ReflectionTestUtils.setField(request, "visitType", VisitType.OUTPATIENT);
        ReflectionTestUtils.setField(request, "treatmentCategory", TreatmentCategory.MRI);
        ReflectionTestUtils.setField(request, "purposeType", PurposeType.TREATMENT);
        ReflectionTestUtils.setField(request, "ediCode", null);
        return request;
    }
}
