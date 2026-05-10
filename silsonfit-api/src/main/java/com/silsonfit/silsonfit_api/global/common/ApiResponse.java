package com.silsonfit.silsonfit_api.global.common;

import com.silsonfit.silsonfit_api.global.error.ErrorCode;
import lombok.Getter;

/**
 * 공통 API 응답 래퍼
 *
 * 성공/에러 모두 동일한 형태로 응답한다.
 * { "code": 200, "message": "success", "data": { } }
 */
@Getter
public class ApiResponse<T> {

    private final int code;
    private final String message;
    private final T data;

    private ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 성공 응답 (데이터 포함)
     *
     * @param data 응답 데이터
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    /**
     * 성공 응답 (데이터 없음)
     */
    public static ApiResponse<Void> success() {
        return new ApiResponse<>(200, "success", null);
    }

    /**
     * 에러 응답
     *
     * @param errorCode 에러 코드 enum
     */
    public static ApiResponse<Void> error(ErrorCode errorCode) {
        return new ApiResponse<>(errorCode.getStatus(), errorCode.getMessage(), null);
    }
}
