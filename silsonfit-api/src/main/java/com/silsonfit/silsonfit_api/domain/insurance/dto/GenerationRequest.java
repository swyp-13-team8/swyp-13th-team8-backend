package com.silsonfit.silsonfit_api.domain.insurance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 세대 판별 요청 DTO
 */
public record GenerationRequest(
        @NotBlank(message = "가입 연월은 필수입니다.")
        @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$", message = "가입 연월 형식은 YYYY-MM이어야 합니다.")
        String subscribedYearMonth
) {
}
