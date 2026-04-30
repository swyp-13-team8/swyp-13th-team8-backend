package com.silsonfit.silsonfit_api.domain.user.dto;

/**
 * 내 프로필 조회 응답 DTO
 */
public record UserResponse(
        Long userId,
        String name,
        String email
) {
}
