package com.silsonfit.silsonfit_api.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 도메인별 에러 코드 정의
 *
 * 각 담당자는 자기 도메인 영역에만 에러 코드를 추가한다.
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // ── Common ──
    INVALID_INPUT(400, "잘못된 입력입니다."),
    INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),

    // ── Auth ──
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(401, "만료된 토큰입니다."),
    INVALID_KAKAO_TOKEN(401, "유효하지 않은 카카오 토큰입니다."),
    KAKAO_SERVER_ERROR(502, "카카오 서버와의 통신에 실패했습니다."),
    REFRESH_TOKEN_NOT_FOUND(401, "리프레시 토큰을 찾을 수 없습니다."),
    REFRESH_TOKEN_EXPIRED(401, "만료된 리프레시 토큰입니다."),
    ALREADY_AGREED_TERMS(409, "이미 약관에 동의한 사용자입니다."),
    INVALID_REFRESH_TOKEN(400, "유효하지 않은 리프레시 토큰입니다."),

    // ── User ──
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다."),
    DEACTIVATED_USER(403, "탈퇴한 사용자입니다."),
    INVALID_NICKNAME(400, "사용할 수 없는 닉네임입니다."),

    // ── Analysis ──
    HISTORY_NOT_FOUND(404, "해당 분석 이력을 찾을 수 없습니다."),
    HISTORY_ACCESS_DENIED(403, "해당 분석 이력에 대한 접근 권한이 없습니다."),

    // ── Insurance ──

    // ── Calculator ──
    COVERAGE_RULE_NOT_FOUND(404, "보장 룰을 찾을 수 없습니다."),

    ;

    private final int status;
    private final String message;
}
