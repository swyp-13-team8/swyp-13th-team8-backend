package com.silsonfit.silsonfit_api.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 토큰 재발급 요청 DTO
 *
 * 클라이언트가 보관 중인 Refresh Token을 전달하여 Access Token 재발급을 요청한다.
 */
public record TokenReissueRequest(
        @NotBlank(message = "refreshToken은 필수입니다.")
        String refreshToken
) {
}
