package com.silsonfit.silsonfit_api.domain.auth.dto;

/**
 * 토큰 재발급 응답 DTO
 *
 * Refresh Token Rotation 전략에 따라 Access와 Refresh Token 모두 새로 발급한다.
 * 응답 후 기존 Refresh Token은 DB에서 무효화된다.
 */
public record TokenReissueResponse(
        String accessToken,
        String refreshToken
) {
}
