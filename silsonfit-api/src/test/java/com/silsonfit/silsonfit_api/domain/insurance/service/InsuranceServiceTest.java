package com.silsonfit.silsonfit_api.domain.insurance.service;

import com.silsonfit.silsonfit_api.domain.insurance.dto.GenerationRequest;
import com.silsonfit.silsonfit_api.domain.insurance.dto.GenerationResponse;
import com.silsonfit.silsonfit_api.domain.insurance.dto.InsuranceDetailResponse;
import com.silsonfit.silsonfit_api.domain.insurance.dto.InsuranceInfoDto;
import com.silsonfit.silsonfit_api.domain.insurance.dto.InsuranceProductResponse;
import com.silsonfit.silsonfit_api.domain.insurance.dto.InsuranceRegisterRequest;
import com.silsonfit.silsonfit_api.domain.insurance.dto.InsuranceRegisterResponse;
import com.silsonfit.silsonfit_api.domain.insurance.dto.UserInsuranceResponse;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * InsuranceService 단위 테스트
 *
 * 세대 판별, 보험 등록/삭제, 상품 목록 조회, 보험 정보 조회 로직 검증
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
                new GenerationRequest("comp_001", "2020-03"));

        // then
        assertThat(response.generation()).isEqualTo(3);
    }

    @Test
    @DisplayName("가입 연월 2023-01 → 4세대 판별")
    void determineGeneration_fourthGen() {
        // when
        GenerationResponse response = insuranceService.determineGeneration(
                new GenerationRequest("comp_001", "2023-01"));

        // then
        assertThat(response.generation()).isEqualTo(4);
    }

    @Test
    @DisplayName("존재하지 않는 보험사 ID로 세대 판별 시 INSURANCE_COMPANY_NOT_FOUND 예외")
    void determineGeneration_invalidCompanyId() {
        // when & then
        assertThatThrownBy(() -> insuranceService.determineGeneration(
                new GenerationRequest("comp_999", "2020-03")))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.INSURANCE_COMPANY_NOT_FOUND);
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

    // ──────────── register ────────────

    @Test
    @DisplayName("보험 등록 성공")
    void register_success() {
        // given
        Insurance insurance = createInsurance(1L, "삼성화재", "삼성화재 실손의료비보험", 3);

        given(userInsuranceRepository.countByUserId(1L)).willReturn(0L);
        given(userInsuranceRepository.existsByUserIdAndInsuranceId(1L, 1L)).willReturn(false);
        given(insuranceRepository.findById(1L)).willReturn(Optional.of(insurance));
        given(userInsuranceRepository.save(any(UserInsurance.class))).willAnswer(invocation -> {
            UserInsurance saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 10L);
            return saved;
        });

        // when
        InsuranceRegisterResponse response = insuranceService.register(
                1L, new InsuranceRegisterRequest(1L, "2020-03"));

        // then
        assertThat(response.userInsuranceId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("보험 5개 초과 등록 시 INSURANCE_LIMIT_EXCEEDED 예외")
    void register_limitExceeded() {
        // given
        given(userInsuranceRepository.countByUserId(1L)).willReturn(5L);

        // when & then
        assertThatThrownBy(() -> insuranceService.register(
                1L, new InsuranceRegisterRequest(1L, "2020-03")))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.INSURANCE_LIMIT_EXCEEDED);
    }

    @Test
    @DisplayName("동일 보험 상품 중복 등록 시 INSURANCE_ALREADY_REGISTERED 예외")
    void register_alreadyRegistered() {
        // given
        given(userInsuranceRepository.countByUserId(1L)).willReturn(1L);
        given(userInsuranceRepository.existsByUserIdAndInsuranceId(1L, 1L)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> insuranceService.register(
                1L, new InsuranceRegisterRequest(1L, "2020-03")))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.INSURANCE_ALREADY_REGISTERED);
    }

    @Test
    @DisplayName("존재하지 않는 보험 상품 등록 시 INSURANCE_NOT_FOUND 예외")
    void register_insuranceNotFound() {
        // given
        given(userInsuranceRepository.countByUserId(1L)).willReturn(0L);
        given(userInsuranceRepository.existsByUserIdAndInsuranceId(1L, 999L)).willReturn(false);
        given(insuranceRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> insuranceService.register(
                1L, new InsuranceRegisterRequest(999L, "2020-03")))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.INSURANCE_NOT_FOUND);
    }

    // NOTE: 기능 명세에 보험 삭제 기능이 없어 주석 처리. 추후 필요 시 해제.
    /*
    @Test
    @DisplayName("보험 삭제 성공")
    void delete_success() {
        Insurance insurance = createInsurance(1L, "삼성화재", "삼성화재 실손의료비보험", 3);
        UserInsurance userInsurance = UserInsurance.builder()
                .userId(1L)
                .insurance(insurance)
                .subscribedAt("2020-03")
                .hasNonCoveredRider(false)
                .build();
        ReflectionTestUtils.setField(userInsurance, "id", 10L);

        given(userInsuranceRepository.findById(10L)).willReturn(Optional.of(userInsurance));

        insuranceService.delete(1L, 10L);

        verify(userInsuranceRepository).delete(userInsurance);
    }

    @Test
    @DisplayName("존재하지 않는 보험 삭제 시 USER_INSURANCE_NOT_FOUND 예외")
    void delete_notFound() {
        given(userInsuranceRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> insuranceService.delete(1L, 999L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.USER_INSURANCE_NOT_FOUND);
    }

    @Test
    @DisplayName("다른 사용자의 보험 삭제 시 USER_INSURANCE_ACCESS_DENIED 예외")
    void delete_accessDenied() {
        Insurance insurance = createInsurance(1L, "삼성화재", "삼성화재 실손의료비보험", 3);
        UserInsurance userInsurance = UserInsurance.builder()
                .userId(1L)
                .insurance(insurance)
                .subscribedAt("2020-03")
                .hasNonCoveredRider(false)
                .build();
        ReflectionTestUtils.setField(userInsurance, "id", 10L);

        given(userInsuranceRepository.findById(10L)).willReturn(Optional.of(userInsurance));

        assertThatThrownBy(() -> insuranceService.delete(2L, 10L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.USER_INSURANCE_ACCESS_DENIED);
    }
    */

    // ──────────── getMyInsurances ────────────

    @Test
    @DisplayName("내 보험 목록 조회 성공")
    void getMyInsurances_success() {
        // given
        Insurance insurance1 = createInsurance(1L, "삼성화재", "삼성화재 실손의료비보험", 3);
        Insurance insurance2 = createInsurance(2L, "현대해상", "현대해상 실손의료비보험", 4);

        UserInsurance ui1 = UserInsurance.builder()
                .userId(1L).insurance(insurance1).subscribedAt("2020-03").hasNonCoveredRider(false).build();
        ReflectionTestUtils.setField(ui1, "id", 10L);

        UserInsurance ui2 = UserInsurance.builder()
                .userId(1L).insurance(insurance2).subscribedAt("2023-01").hasNonCoveredRider(false).build();
        ReflectionTestUtils.setField(ui2, "id", 11L);

        given(userInsuranceRepository.findByUserIdWithInsurance(1L))
                .willReturn(List.of(ui1, ui2));

        // when
        List<UserInsuranceResponse> result = insuranceService.getMyInsurances(1L);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).companyName()).isEqualTo("삼성화재");
        assertThat(result.get(0).joinDate()).isEqualTo("2020-03");
        assertThat(result.get(0).contractType()).isEqualTo("개인실손");
        assertThat(result.get(0).coverageStructure()).isEqualTo("급여+비급여");
        assertThat(result.get(0).cautionPoint()).isEqualTo("갱신형");
        assertThat(result.get(1).companyName()).isEqualTo("현대해상");
        assertThat(result.get(1).generation()).isEqualTo(4);
    }

    @Test
    @DisplayName("등록된 보험이 없으면 빈 목록 반환")
    void getMyInsurances_empty() {
        // given
        given(userInsuranceRepository.findByUserIdWithInsurance(1L))
                .willReturn(List.of());

        // when
        List<UserInsuranceResponse> result = insuranceService.getMyInsurances(1L);

        // then
        assertThat(result).isEmpty();
    }

    // ──────────── getProducts ────────────

    @Test
    @DisplayName("보험사별 상품 목록 조회 성공")
    void getProducts_success() {
        // given
        List<Insurance> products = List.of(
                createInsurance(1L, "삼성화재", "무배당 삼성화재 실손의료비보험 3세대", 3),
                createInsurance(2L, "삼성화재", "무배당 삼성화재 유병력자 실손의료비보험 3세대", 3)
        );

        given(insuranceRepository.findByCompanyNameAndGeneration("삼성화재", 3))
                .willReturn(products);

        // when
        List<InsuranceProductResponse> result = insuranceService.getProducts("삼성화재", 3);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).productName()).isEqualTo("무배당 삼성화재 실손의료비보험 3세대");
        assertThat(result.get(0).contractType()).isEqualTo("개인실손");
        assertThat(result.get(0).generation()).isEqualTo(3);
        assertThat(result.get(0).coverageStructure()).isEqualTo("급여+비급여");
        assertThat(result.get(0).cautionPoint()).isEqualTo("갱신형");
    }

    @Test
    @DisplayName("기타 보험사 선택 시 표준약관 반환")
    void getProducts_etc_standardPolicy() {
        // given
        Insurance standardPolicy = createInsurance(100L, "표준약관", "1세대 표준약관", 1);
        given(insuranceRepository.findByCompanyNameAndGeneration("표준약관", 1))
                .willReturn(List.of(standardPolicy));

        // when
        List<InsuranceProductResponse> result = insuranceService.getProducts("기타", 1);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).productName()).isEqualTo("1세대 표준약관");
    }

    @Test
    @DisplayName("존재하지 않는 보험사명으로 조회 시 INSURANCE_COMPANY_NOT_FOUND 예외")
    void getProducts_invalidCompanyName() {
        // when & then
        assertThatThrownBy(() -> insuranceService.getProducts("없는보험사", 3))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.INSURANCE_COMPANY_NOT_FOUND);
    }

    @Test
    @DisplayName("빅5 보험사 상품 없으면 표준약관 fallback")
    void getProducts_bigFive_fallbackToStandard() {
        // given — 메리츠 2세대 상품 없음
        given(insuranceRepository.findByCompanyNameAndGeneration("메리츠화재", 2))
                .willReturn(List.of());

        Insurance standardPolicy = createInsurance(100L, "표준약관", "2세대 표준약관", 2);
        given(insuranceRepository.findByCompanyNameAndGeneration("표준약관", 2))
                .willReturn(List.of(standardPolicy));

        // when
        List<InsuranceProductResponse> result = insuranceService.getProducts("메리츠화재", 2);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).productName()).isEqualTo("2세대 표준약관");
    }

    @Test
    @DisplayName("표준약관도 없으면 INSURANCE_NOT_FOUND 예외")
    void getProducts_noStandardPolicy() {
        // given
        given(insuranceRepository.findByCompanyNameAndGeneration("표준약관", 1))
                .willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> insuranceService.getProducts("기타", 1))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.INSURANCE_NOT_FOUND);
    }

    // ──────────── getInsuranceDetail ────────────

    @Test
    @DisplayName("등록 보험 상세 조회 성공")
    void getInsuranceDetail_success() {
        // given
        Insurance insurance = createInsurance(1L, "삼성화재", "삼성화재 실손의료비보험", 3);
        UserInsurance userInsurance = UserInsurance.builder()
                .userId(1L)
                .insurance(insurance)
                .subscribedAt("2020-03")
                .hasNonCoveredRider(false)
                .build();
        ReflectionTestUtils.setField(userInsurance, "id", 10L);

        given(userInsuranceRepository.findById(10L)).willReturn(Optional.of(userInsurance));

        // when
        InsuranceDetailResponse result = insuranceService.getInsuranceDetail(1L, 10L);

        // then
        assertThat(result.userInsuranceId()).isEqualTo(10L);
        assertThat(result.companyName()).isEqualTo("삼성화재");
        assertThat(result.productName()).isEqualTo("삼성화재 실손의료비보험");
        assertThat(result.generation()).isEqualTo(3);
        assertThat(result.joinDate()).isEqualTo("2020-03");
        assertThat(result.contractType()).isEqualTo("개인실손");
        assertThat(result.coverageStructure()).isEqualTo("급여+비급여");
        assertThat(result.cautionPoint()).isEqualTo("갱신형");
    }

    @Test
    @DisplayName("존재하지 않는 보험 상세 조회 시 USER_INSURANCE_NOT_FOUND 예외")
    void getInsuranceDetail_notFound() {
        // given
        given(userInsuranceRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> insuranceService.getInsuranceDetail(1L, 999L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.USER_INSURANCE_NOT_FOUND);
    }

    @Test
    @DisplayName("다른 사용자의 보험 상세 조회 시 USER_INSURANCE_ACCESS_DENIED 예외")
    void getInsuranceDetail_accessDenied() {
        // given
        Insurance insurance = createInsurance(1L, "삼성화재", "삼성화재 실손의료비보험", 3);
        UserInsurance userInsurance = UserInsurance.builder()
                .userId(1L)
                .insurance(insurance)
                .subscribedAt("2020-03")
                .hasNonCoveredRider(false)
                .build();
        ReflectionTestUtils.setField(userInsurance, "id", 10L);

        given(userInsuranceRepository.findById(10L)).willReturn(Optional.of(userInsurance));

        // when & then
        assertThatThrownBy(() -> insuranceService.getInsuranceDetail(2L, 10L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.USER_INSURANCE_ACCESS_DENIED);
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

    // ──────────── helper ────────────

    private Insurance createInsurance(Long id, String companyName, String productName, int generation) {
        Insurance insurance = Insurance.builder()
                .companyName(companyName)
                .productName(productName)
                .contractType(ContractType.INDIVIDUAL)
                .generation(generation)
                .coverageStructure(CoverageStructure.COVERED_AND_UNCOVERED)
                .cautionPoint(CautionPoint.RENEWAL_TYPE)
                .build();
        ReflectionTestUtils.setField(insurance, "id", id);
        return insurance;
    }
}
