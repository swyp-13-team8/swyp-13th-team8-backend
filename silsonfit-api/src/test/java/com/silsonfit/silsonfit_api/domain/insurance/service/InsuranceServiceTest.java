package com.silsonfit.silsonfit_api.domain.insurance.service;

import com.silsonfit.silsonfit_api.domain.insurance.dto.GenerationRequest;
import com.silsonfit.silsonfit_api.domain.insurance.dto.GenerationResponse;
import com.silsonfit.silsonfit_api.domain.insurance.dto.InsuranceInfoDto;
import com.silsonfit.silsonfit_api.domain.insurance.enums.CautionPoint;
import com.silsonfit.silsonfit_api.domain.insurance.enums.ContractType;
import com.silsonfit.silsonfit_api.domain.insurance.enums.CoverageStructure;
import com.silsonfit.silsonfit_api.domain.insurance.entity.Insurance;
import com.silsonfit.silsonfit_api.domain.insurance.entity.UserInsurance;
import com.silsonfit.silsonfit_api.domain.insurance.repository.InsuranceRepository;
import com.silsonfit.silsonfit_api.domain.insurance.repository.UserInsuranceRepository;
import com.silsonfit.silsonfit_api.global.error.BusinessException;
import com.silsonfit.silsonfit_api.global.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

/**
 * InsuranceService 단위 테스트
 *
 * 세대 판별 및 보험 정보 조회 로직 검증
 */
@ExtendWith(MockitoExtension.class)
class InsuranceServiceTest {

    @Mock
    private InsuranceRepository insuranceRepository;

    @Mock
    private UserInsuranceRepository userInsuranceRepository;

    @InjectMocks
    private InsuranceService insuranceService;

    // ──────────── determineGeneration ────────────

    @Test
    @DisplayName("가입 연월 2020-03 → 3세대 판별")
    void determineGeneration_thirdGen() {
        // when
        GenerationResponse response = insuranceService.determineGeneration(
                new GenerationRequest("2020-03"));

        // then
        assertThat(response.generation()).isEqualTo(3);
        assertThat(response.description()).contains("3세대");
    }

    @Test
    @DisplayName("가입 연월 2023-01 → 4세대 판별")
    void determineGeneration_fourthGen() {
        // when
        GenerationResponse response = insuranceService.determineGeneration(
                new GenerationRequest("2023-01"));

        // then
        assertThat(response.generation()).isEqualTo(4);
    }

    // ──────────── getInsuranceInfo ────────────

    @Test
    @DisplayName("보험 정보 조회 성공")
    void getInsuranceInfo_success() {
        // given
        Insurance insurance = Insurance.builder()
                .companyName("삼성화재")
                .productName("삼성화재 실손의료비보험")
                .contractType(ContractType.INDIVIDUAL)
                .generation(3)
                .coverageStructure(CoverageStructure.COVERED_AND_UNCOVERED)
                .cautionPoint(CautionPoint.RENEWAL_TYPE)
                .pdfFileUrl("https://s3.../samsung.pdf")
                .pdfFileName("삼성화재_약관.pdf")
                .build();
        ReflectionTestUtils.setField(insurance, "id", 1L);

        UserInsurance userInsurance = UserInsurance.builder()
                .userId(1L)
                .insurance(insurance)
                .subscribedAt("2020-03")
                .hasNonCoveredRider(false)
                .build();
        ReflectionTestUtils.setField(userInsurance, "id", 10L);

        given(userInsuranceRepository.findById(10L)).willReturn(Optional.of(userInsurance));

        // when
        InsuranceInfoDto result = insuranceService.getInsuranceInfo(10L);

        // then
        assertThat(result.companyName()).isEqualTo("삼성화재");
        assertThat(result.productName()).isEqualTo("삼성화재 실손의료비보험");
        assertThat(result.contractType()).isEqualTo(ContractType.INDIVIDUAL);
        assertThat(result.generation()).isEqualTo(3);
        assertThat(result.coverageStructure()).isEqualTo(CoverageStructure.COVERED_AND_UNCOVERED);
        assertThat(result.cautionPoint()).isEqualTo(CautionPoint.RENEWAL_TYPE);
        assertThat(result.pdfFileUrl()).isEqualTo("https://s3.../samsung.pdf");
        assertThat(result.subscribedAt()).isEqualTo("2020-03");
        assertThat(result.hasNonCoveredRider()).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 사용자 보험 조회 시 USER_INSURANCE_NOT_FOUND 예외")
    void getInsuranceInfo_notFound() {
        // given
        given(userInsuranceRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> insuranceService.getInsuranceInfo(999L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.USER_INSURANCE_NOT_FOUND);
    }

    // ──────────── getGenerationByInsuranceId ────────────

    @Test
    @DisplayName("보험 ID로 세대 조회 성공")
    void getGenerationByInsuranceId_success() {
        // given
        Insurance insurance = Insurance.builder()
                .companyName("삼성화재")
                .productName("삼성화재 실손의료비보험")
                .contractType(ContractType.INDIVIDUAL)
                .generation(3)
                .coverageStructure(CoverageStructure.COVERED_AND_UNCOVERED)
                .cautionPoint(CautionPoint.RENEWAL_TYPE)
                .build();
        ReflectionTestUtils.setField(insurance, "id", 1L);

        given(insuranceRepository.findById(1L)).willReturn(Optional.of(insurance));

        // when
        int generation = insuranceService.getGenerationByInsuranceId(1L);

        // then
        assertThat(generation).isEqualTo(3);
    }

    @Test
    @DisplayName("존재하지 않는 보험 ID로 세대 조회 시 INSURANCE_NOT_FOUND 예외")
    void getGenerationByInsuranceId_notFound() {
        // given
        given(insuranceRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> insuranceService.getGenerationByInsuranceId(999L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.INSURANCE_NOT_FOUND);
    }
}
