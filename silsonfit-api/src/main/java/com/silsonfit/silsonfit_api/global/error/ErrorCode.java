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

    // ── User ──
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다."),
    DEACTIVATED_USER(403, "탈퇴한 사용자입니다."),

    // ── Insurance ──

    // ── Calculator ──

    ;

    private final int status;
    private final String message;
}
