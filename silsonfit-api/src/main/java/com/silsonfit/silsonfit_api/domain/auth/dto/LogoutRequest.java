package com.silsonfit.silsonfit_api.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 로그아웃 요청 DTO
 */
public record LogoutRequest(
        @NotBlank(message = "refreshToken은 필수입니다.")
        String refreshToken
) {
}
