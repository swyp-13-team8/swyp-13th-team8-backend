package com.silsonfit.silsonfit_api.domain.calculation.controller;

import com.silsonfit.silsonfit_api.domain.calculation.dto.CalculationHistoryListResponse;
import com.silsonfit.silsonfit_api.domain.calculation.service.CalculationHistoryService;
import com.silsonfit.silsonfit_api.global.auth.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalculationHistoryController.class)
class CalculationHistoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CalculationHistoryService calculationHistoryService;

    @Test
    @DisplayName("계산 이력 목록을 조회한다")
    void getHistories_success() throws Exception {
        CalculationHistoryListResponse response = new CalculationHistoryListResponse(
                List.of(new CalculationHistoryListResponse.CalculationSummary(
                        "calc_123",
                        OffsetDateTime.parse("2024-06-01T10:00:00Z"),
                        "MRI",
                        3000000,
                        2500000,
                        true
                )),
                new CalculationHistoryListResponse.PageInfo(0, 20, 3, 45)
        );

        when(calculationHistoryService.getHistories(eq(1L), eq(PageRequest.of(0, 20))))
                .thenReturn(response);

        mockMvc.perform(get("/api/calculations")
                        .param("page", "0")
                        .param("size", "20")
                        .with(user(new CustomUserDetails(1L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.calculations[0].id").value("calc_123"))
                .andExpect(jsonPath("$.data.calculations[0].calculatedDate").value("2024-06-01T10:00:00Z"))
                .andExpect(jsonPath("$.data.calculations[0].insuranceCoverage").value("MRI"))
                .andExpect(jsonPath("$.data.calculations[0].medicalCost").value(3000000))
                .andExpect(jsonPath("$.data.calculations[0].refundAmount").value(2500000))
                .andExpect(jsonPath("$.data.calculations[0].isCovered").value(true))
                .andExpect(jsonPath("$.data.pageInfo.page").value(0))
                .andExpect(jsonPath("$.data.pageInfo.size").value(20))
                .andExpect(jsonPath("$.data.pageInfo.totalPages").value(3))
                .andExpect(jsonPath("$.data.pageInfo.totalElements").value(45));

        verify(calculationHistoryService).getHistories(eq(1L), eq(PageRequest.of(0, 20)));
    }

    @Test
    @DisplayName("계산 이력을 삭제한다")
    void deleteHistory_success() throws Exception {
        mockMvc.perform(delete("/api/calculations/{calculationHistoryId}", 1L)
                        .with(user(new CustomUserDetails(1L)))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isEmpty());

        verify(calculationHistoryService).deleteHistory(1L, 1L);
    }
}
