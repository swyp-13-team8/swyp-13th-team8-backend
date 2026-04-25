package com.silsonfit.silsonfit_api.global.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * SecurityConfig 통합 테스트
 *
 * 경로별 인증 정책 검증
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    // ──────────── 비인증 경로 (permitAll) ────────────

    @Test
    @DisplayName("Swagger UI는 토큰 없이 접근 가능")
    void swaggerUi_permitAll() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Actuator health는 토큰 없이 접근 가능")
    void actuatorHealth_permitAll() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    // ──────────── 인증 경로 (authenticated) ────────────

    @Test
    @DisplayName("인증 필요한 경로에 토큰 없이 접근 시 401 + ApiResponse 형태 반환")
    void protectedEndpoint_without_token_returns401() throws Exception {
        mockMvc.perform(get("/api/protected/test"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("인증이 필요합니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
