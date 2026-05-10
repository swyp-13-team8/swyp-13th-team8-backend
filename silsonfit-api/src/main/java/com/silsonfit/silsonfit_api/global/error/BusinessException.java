package com.silsonfit.silsonfit_api.global.error;

import lombok.Getter;

/**
 * 비즈니스 예외 정의 클래스
 *
 * ErrorCode를 담아서 던지면 GlobalExceptionHandler가 잡아서 처리한다.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
