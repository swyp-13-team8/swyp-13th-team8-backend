package com.silsonfit.silsonfit_api.domain.auth.dto;

/**
 * 카카오 로그인 응답 DTO
 *
 * 서비스 Access/Refresh Token과 신규 가입 여부 반환
 * isNewUser가 true이면 클라이언트는 약관 동의 화면으로 분기
 */
public record LoginResponse(
        String accessToken,
        String refreshToken,
        boolean isNewUser
) {
}
