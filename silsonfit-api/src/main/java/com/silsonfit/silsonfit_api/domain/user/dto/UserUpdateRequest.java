package com.silsonfit.silsonfit_api.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 닉네임 수정 요청 DTO
 */
public record UserUpdateRequest(
        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(max = 50, message = "닉네임은 50자 이내여야 합니다.")
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$", message = "한글, 영문, 숫자만 사용 가능합니다.")
        String name
) {
}
