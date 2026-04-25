package com.silsonfit.silsonfit_api.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 카카오 로그인 요청 DTO
 *
 * 클라이언트가 카카오 SDK로 발급받은 Access Token 전달
 */
public record LoginRequest(
        @NotBlank(message = "kakaoToken은 필수입니다.")
        String kakaoToken
) {
}
