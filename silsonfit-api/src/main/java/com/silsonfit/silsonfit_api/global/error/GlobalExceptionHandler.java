package com.silsonfit.silsonfit_api.global.error;

import com.silsonfit.silsonfit_api.global.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 글로벌 예외 핸들러
 *
 * 모든 예외를 잡아서 ApiResponse 형태로 변환한다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    // 비즈니스 예외 처리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn("BusinessException: {} - {}", errorCode.getStatus(), errorCode.getMessage());
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode));
    }



    // @Valid 검증 실패 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        log.warn("ValidationException: {}", e.getBindingResult().getAllErrors());
        return ResponseEntity
                .status(400)
                .body(ApiResponse.error(ErrorCode.INVALID_INPUT));
    }


    // 예상하지 못한 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("UnhandledException: ", e);
        return ResponseEntity
                .status(500)
                .body(ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
