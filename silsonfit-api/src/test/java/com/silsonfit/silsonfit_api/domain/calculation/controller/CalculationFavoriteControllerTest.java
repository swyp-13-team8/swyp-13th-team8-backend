package com.silsonfit.silsonfit_api.domain.calculation.controller;

import com.silsonfit.silsonfit_api.domain.calculation.dto.CalculationFavoriteResponse;
import com.silsonfit.silsonfit_api.domain.calculation.service.CalculationFavoriteService;
import com.silsonfit.silsonfit_api.global.auth.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalculationFavoriteController.class)
class CalculationFavoriteControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CalculationFavoriteService calculationFavoriteService;

    @Test
    @DisplayName("계산 이력 즐겨찾기를 토글한다")
    void toggleFavorite_success() throws Exception {
        mockMvc.perform(patch("/api/calculations/{calculationHistoryId}/favorite", 1L)
                        .with(user(new CustomUserDetails(10L)))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isEmpty());

        verify(calculationFavoriteService).toggleFavorite(10L, 1L);
    }

    @Test
    @DisplayName("계산 이력 즐겨찾기 목록을 조회한다")
    void getFavorites_success() throws Exception {
        when(calculationFavoriteService.getFavorites(10L)).thenReturn(List.of(
                new CalculationFavoriteResponse(
                        "calc_1",
                        OffsetDateTime.parse("2026-05-04T10:00:00Z"),
                        "MRI",
                        100000,
                        70000,
                        true,
                        true
                )
        ));

        mockMvc.perform(get("/api/calculations/favorites")
                        .with(user(new CustomUserDetails(10L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data[0].id").value("calc_1"))
                .andExpect(jsonPath("$.data[0].calculatedDate").value("2026-05-04T10:00:00Z"))
                .andExpect(jsonPath("$.data[0].insuranceCoverage").value("MRI"))
                .andExpect(jsonPath("$.data[0].medicalCost").value(100000))
                .andExpect(jsonPath("$.data[0].refundAmount").value(70000))
                .andExpect(jsonPath("$.data[0].isCovered").value(true))
                .andExpect(jsonPath("$.data[0].isFavorite").value(true));
    }

}
