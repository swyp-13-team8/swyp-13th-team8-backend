package com.silsonfit.silsonfit_api.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 도메인별 에러 코드 정의
 * <p>
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
    WITHDRAW_GRACE_PERIOD_EXPIRED(403, "탈퇴 후 30일이 경과하여 복구할 수 없습니다."),
    INVALID_NICKNAME(400, "사용할 수 없는 닉네임입니다."),

    // ── Analysis ──
    INVALID_REQUEST(400,"보험 ID나 약관 PDF 파일 중 하나는 필수 입니다."),
    HISTORY_NOT_FOUND(404, "해당 분석 이력을 찾을 수 없습니다."),
    HISTORY_ACCESS_DENIED(403, "해당 분석 이력에 대한 접근 권한이 없습니다."),
    PDF_PARSING_ERROR(500, "PDF 파일에서 텍스트를 추출하는데 실패했습니다."),
    AI_ANALYSIS_FAILED(500, "AI 약관 분석 처리 중 오류가 발생했습니다"),

    // ── File ──
    FILE_READ_FAILED(500,"파일을 읽는 중 오류가 발생했습니다."),
    FILE_UPLOAD_FAILED(500, "파일을 업로드하는 중 오류가 발생했습니다."),
    FILE_DELETE_FAILED(500, "파일을 삭제하는 중 오류가 발생했습니다."),

    // ── Insurance ──
    INSURANCE_COMPANY_NOT_FOUND(400, "존재하지 않는 보험사입니다."),
    INSURANCE_NOT_FOUND(404, "보험 상품을 찾을 수 없습니다."),
    USER_INSURANCE_NOT_FOUND(404, "등록된 보험을 찾을 수 없습니다."),
    INVALID_SUBSCRIBED_DATE(400, "세대를 판별할 수 없는 가입 연월입니다."),
    INSURANCE_LIMIT_EXCEEDED(400, "보험은 최대 5개까지 등록할 수 있습니다."),
    INSURANCE_ALREADY_REGISTERED(409, "이미 등록된 보험 상품입니다."),
    USER_INSURANCE_ACCESS_DENIED(403, "해당 보험에 대한 접근 권한이 없습니다."),

    // ── Calculator ──
    COVERAGE_RULE_NOT_FOUND(404, "보장 룰을 찾을 수 없습니다."),
    EDI_CODE_NOT_FOUND(404, "EDI 코드를 찾을 수 없습니다."),
    EDI_API_SERVER_ERROR(502, "공공 EDI API 통신에 실패했습니다."),
    CALCULATION_HISTORY_NOT_FOUND(404, "계산 이력을 찾을 수 없습니다."),
    CALCULATION_HISTORY_ACCESS_DENIED(403, "해당 계산 이력에 대한 접근 권한이 없습니다."),

    ;

    private final int status;
    private final String message;
}
