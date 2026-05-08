package com.silsonfit.silsonfit_api.domain.calculation.controller;

import com.silsonfit.silsonfit_api.domain.calculation.dto.CalculationRequest;
import com.silsonfit.silsonfit_api.domain.calculation.dto.CalculationResponse;
import com.silsonfit.silsonfit_api.domain.calculation.enums.CoverageStatus;
import com.silsonfit.silsonfit_api.domain.calculation.enums.HospitalType;
import com.silsonfit.silsonfit_api.domain.calculation.enums.PayType;
import com.silsonfit.silsonfit_api.domain.calculation.enums.PurposeType;
import com.silsonfit.silsonfit_api.domain.calculation.enums.TreatmentCategory;
import com.silsonfit.silsonfit_api.domain.calculation.enums.VisitType;
import com.silsonfit.silsonfit_api.domain.calculation.service.CalculationService;
import com.silsonfit.silsonfit_api.global.auth.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalculationController.class)
class CalculationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CalculationService calculationService;

    @Test
    @DisplayName("계산 요청을 처리하고 ApiResponse로 계산 결과를 반환한다")
    void calculate_success() throws Exception {
        CalculationResponse response = CalculationResponse.builder()
                .isCovered(CoverageStatus.PARTIAL_COVERED)
                .refundAmount(70000)
                .deductibleAmount(30000)
                .basis("계산 근거")
                .deductibleBasis("30,000원 또는 진료비의 30% 중 큰 금액")
                .disclaimer("주의사항")
                .treatmentInfos(List.of("외래", "MRI", "비급여"))
                .totalMedicalCost(100000)
                .productName("무배당 실손의료비보험")
                .companyName("삼성화재")
                .insuranceInfos(List.of("4세대", "3대비급여"))
                .joinDate("2025-04")
                .deductibleRate(30)
                .refundRate(70)
                .ediCode("EDI001")
                .build();

        when(calculationService.calculate(eq(1L), any())).thenReturn(response);

        mockMvc.perform(post("/api/calculations")
                        .with(user(new CustomUserDetails(1L)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "insuranceId": "ins_1",
                                  "medicalCost": 100000,
                                  "visitType": "OUTPATIENT",
                                  "treatmentCategory": "MRI",
                                  "purposeType": "TREATMENT",
                                  "ediCode": "EDI001",
                                  "hospitalType": "GENERAL_HOSPITAL",
                                  "payType": "NON_PAY"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.isCovered").value("PARTIAL_COVERED"))
                .andExpect(jsonPath("$.data.refundAmount").value(70000))
                .andExpect(jsonPath("$.data.deductibleAmount").value(30000))
                .andExpect(jsonPath("$.data.basis").value("계산 근거"))
                .andExpect(jsonPath("$.data.deductibleBasis").value("30,000원 또는 진료비의 30% 중 큰 금액"))
                .andExpect(jsonPath("$.data.disclaimer").value("주의사항"))
                .andExpect(jsonPath("$.data.treatmentInfos[0]").value("외래"))
                .andExpect(jsonPath("$.data.treatmentInfos[1]").value("MRI"))
                .andExpect(jsonPath("$.data.treatmentInfos[2]").value("비급여"))
                .andExpect(jsonPath("$.data.totalMedicalCost").value(100000))
                .andExpect(jsonPath("$.data.productName").value("무배당 실손의료비보험"))
                .andExpect(jsonPath("$.data.companyName").value("삼성화재"))
                .andExpect(jsonPath("$.data.insuranceInfos[0]").value("4세대"))
                .andExpect(jsonPath("$.data.insuranceInfos[1]").value("3대비급여"))
                .andExpect(jsonPath("$.data.joinDate").value("2025-04"))
                .andExpect(jsonPath("$.data.deductibleRate").value(30))
                .andExpect(jsonPath("$.data.refundRate").value(70))
                .andExpect(jsonPath("$.data.ediCode").value("EDI001"));

        org.mockito.ArgumentCaptor<CalculationRequest> requestCaptor =
                org.mockito.ArgumentCaptor.forClass(CalculationRequest.class);
        verify(calculationService).calculate(eq(1L), requestCaptor.capture());

        CalculationRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.getInsuranceId()).isEqualTo("ins_1");
        assertThat(capturedRequest.getMedicalCost()).isEqualTo(100000);
        assertThat(capturedRequest.getVisitType()).isEqualTo(VisitType.OUTPATIENT);
        assertThat(capturedRequest.getTreatmentCategory()).isEqualTo(TreatmentCategory.MRI);
        assertThat(capturedRequest.getPurposeType()).isEqualTo(PurposeType.TREATMENT);
        assertThat(capturedRequest.getEdiCode()).isEqualTo("EDI001");
        assertThat(capturedRequest.getHospitalType()).isEqualTo(HospitalType.GENERAL_HOSPITAL);
        assertThat(capturedRequest.getPayType()).isEqualTo(PayType.NON_PAY);
    }

    @Test
    @DisplayName("필수 요청값이 없으면 400 응답을 반환한다")
    void calculate_invalidRequest() throws Exception {
        mockMvc.perform(post("/api/calculations")
                        .with(user(new CustomUserDetails(1L)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "insuranceId": "ins_1",
                                  "medicalCost": 100000,
                                  "visitType": "OUTPATIENT",
                                  "treatmentCategory": "MRI",
                                  "hospitalType": "GENERAL_HOSPITAL",
                                  "payType": "NON_PAY"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("잘못된 입력입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
