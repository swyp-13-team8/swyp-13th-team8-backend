package com.silsonfit.silsonfit_api.domain.calculation.service;

import com.silsonfit.silsonfit_api.domain.calculation.dto.CalculationRequest;
import com.silsonfit.silsonfit_api.domain.calculation.dto.CalculationResponse;
import com.silsonfit.silsonfit_api.domain.calculation.entity.CalculationHistory;
import com.silsonfit.silsonfit_api.domain.calculation.entity.CoverageRule;
import com.silsonfit.silsonfit_api.domain.calculation.enums.CoverageStatus;
import com.silsonfit.silsonfit_api.domain.calculation.enums.HospitalType;
import com.silsonfit.silsonfit_api.domain.calculation.enums.InsuranceGeneration;
import com.silsonfit.silsonfit_api.domain.calculation.enums.PayType;
import com.silsonfit.silsonfit_api.domain.calculation.enums.PurposeType;
import com.silsonfit.silsonfit_api.domain.calculation.enums.TreatmentCategory;
import com.silsonfit.silsonfit_api.domain.calculation.enums.VisitType;
import com.silsonfit.silsonfit_api.domain.calculation.repository.CalculationHistoryRepository;
import com.silsonfit.silsonfit_api.domain.calculation.repository.CoverageRuleRepository;
import com.silsonfit.silsonfit_api.domain.insurance.entity.Insurance;
import com.silsonfit.silsonfit_api.domain.insurance.entity.UserInsurance;
import com.silsonfit.silsonfit_api.domain.insurance.enums.CautionPoint;
import com.silsonfit.silsonfit_api.domain.insurance.enums.ContractType;
import com.silsonfit.silsonfit_api.domain.insurance.enums.CoverageStructure;
import com.silsonfit.silsonfit_api.domain.insurance.repository.InsuranceRepository;
import com.silsonfit.silsonfit_api.domain.insurance.repository.UserInsuranceRepository;
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

    private static final Long TEST_USER_ID = 1L;

    @Autowired
    CalculationService calculationService;

    @Autowired
    CoverageRuleRepository coverageRuleRepository;

    @Autowired
    CalculationHistoryRepository calculationHistoryRepository;

    @Autowired
    InsuranceRepository insuranceRepository;

    @Autowired
    UserInsuranceRepository userInsuranceRepository;

    @Test
    void 보장_대상이면_보장률에_따라_환급액과_자기부담금을_계산한다() {
        Long userInsuranceId = saveUserInsurance();
        coverageRuleRepository.save(createCoverageRule(
                true,
                70,
                10000,
                null
        ));
        CalculationRequest request = createCalculationRequest(userInsuranceId, 100000);

        CalculationResponse response = calculationService.calculate(TEST_USER_ID, request);

        assertThat(response.getIsCovered()).isEqualTo(CoverageStatus.PARTIAL_COVERED);
        assertThat(response.getRefundAmount()).isEqualTo(70000);
        assertThat(response.getDeductibleAmount()).isEqualTo(30000);
        assertThat(response.getBasis()).isEqualTo("계산 테스트 룰");
        assertThat(response.getDeductibleBasis()).isEqualTo("10,000원 또는 진료비의 30% 중 큰 금액");
        assertThat(response.getTreatmentInfos()).containsExactly("외래", "CT", "급여 여부 모름");
        assertThat(response.getTotalMedicalCost()).isEqualTo(100000);
        assertThat(response.getProductName()).isEqualTo("테스트 실손보험");
        assertThat(response.getCompanyName()).isEqualTo("삼성화재");
        assertThat(response.getInsuranceInfos()).containsExactly("4세대", "3대비급여", "비급여특약");
        assertThat(response.getJoinDate()).isEqualTo("2025-04");
        assertThat(response.getDeductibleRate()).isEqualTo(30);
        assertThat(response.getRefundRate()).isEqualTo(70);

        CalculationHistory history = calculationHistoryRepository.findAll().get(0);
        assertThat(history.getUserId()).isEqualTo(TEST_USER_ID);
        assertThat(history.getInsuranceId()).isEqualTo(userInsuranceId);
        assertThat(history.getMedicalCost()).isEqualTo(100000);
        assertThat(history.getTreatmentCategory()).isEqualTo(TreatmentCategory.CT);
        assertThat(history.getRefundAmount()).isEqualTo(70000);
        assertThat(history.getDeductibleAmount()).isEqualTo(30000);
    }

    @Test
    void 고정_자기부담금이_더_크면_고정_자기부담금을_적용한다() {
        Long userInsuranceId = saveUserInsurance();
        coverageRuleRepository.save(createCoverageRule(
                true,
                95,
                10000,
                null
        ));
        CalculationRequest request = createCalculationRequest(userInsuranceId, 100000);

        CalculationResponse response = calculationService.calculate(TEST_USER_ID, request);

        assertThat(response.getIsCovered()).isEqualTo(CoverageStatus.PARTIAL_COVERED);
        assertThat(response.getRefundAmount()).isEqualTo(90000);
        assertThat(response.getDeductibleAmount()).isEqualTo(10000);
    }

    @Test
    void 보장_제외이면_환급액은_0원이고_자기부담금은_의료비_전액이다() {
        Long userInsuranceId = saveUserInsurance();
        coverageRuleRepository.save(createCoverageRule(
                false,
                0,
                0,
                null
        ));
        CalculationRequest request = createCalculationRequest(userInsuranceId, 100000);

        CalculationResponse response = calculationService.calculate(TEST_USER_ID, request);

        assertThat(response.getIsCovered()).isEqualTo(CoverageStatus.NOT_COVERED);
        assertThat(response.getRefundAmount()).isZero();
        assertThat(response.getDeductibleAmount()).isEqualTo(100000);
        assertThat(response.getDeductibleBasis()).isEqualTo("보장 제외로 진료비 전액 자기부담");
    }

    private CoverageRule createCoverageRule(
            Boolean isCovered,
            Integer coverageRate,
            Integer deductibleAmount,
            Integer limitAmount
    ) {
        return CoverageRule.create(
                null,
                InsuranceGeneration.FOURTH,
                null,
                VisitType.OUTPATIENT,
                TreatmentCategory.CT,
                PurposeType.UNKNOWN,
                isCovered,
                coverageRate,
                deductibleAmount,
                limitAmount,
                List.of("계산 테스트 룰"),
                null
        );
    }

    private Long saveUserInsurance() {
        Insurance insurance = insuranceRepository.save(Insurance.builder()
                .companyName("삼성화재")
                .productName("테스트 실손보험")
                .contractType(ContractType.INDIVIDUAL)
                .generation(4)
                .coverageStructure(CoverageStructure.THREE_UNCOVERED)
                .cautionPoint(CautionPoint.RENEWAL_TYPE)
                .build());

        UserInsurance userInsurance = userInsuranceRepository.save(UserInsurance.builder()
                .userId(TEST_USER_ID)
                .insurance(insurance)
                .subscribedAt("2025-04")
                .hasNonCoveredRider(true)
                .build());

        return userInsurance.getId();
    }

    private CalculationRequest createCalculationRequest(Long userInsuranceId, Integer medicalCost) {
        CalculationRequest request = new CalculationRequest();
        ReflectionTestUtils.setField(request, "insuranceId", "ins_" + userInsuranceId);
        ReflectionTestUtils.setField(request, "medicalCost", medicalCost);
        ReflectionTestUtils.setField(request, "visitType", VisitType.OUTPATIENT);
        ReflectionTestUtils.setField(request, "treatmentCategory", TreatmentCategory.CT);
        ReflectionTestUtils.setField(request, "purposeType", PurposeType.UNKNOWN);
        ReflectionTestUtils.setField(request, "ediCode", null);
        ReflectionTestUtils.setField(request, "hospitalType", HospitalType.UNKNOWN);
        ReflectionTestUtils.setField(request, "payType", PayType.UNKNOWN);
        return request;
    }
}
