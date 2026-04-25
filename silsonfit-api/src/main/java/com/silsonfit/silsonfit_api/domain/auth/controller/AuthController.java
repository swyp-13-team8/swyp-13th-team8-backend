package com.silsonfit.silsonfit_api.domain.auth.controller;

import com.silsonfit.silsonfit_api.domain.auth.dto.LoginRequest;
import com.silsonfit.silsonfit_api.domain.auth.dto.LoginResponse;
import com.silsonfit.silsonfit_api.domain.auth.dto.TokenReissueRequest;
import com.silsonfit.silsonfit_api.domain.auth.dto.TokenReissueResponse;
import com.silsonfit.silsonfit_api.domain.auth.service.AuthService;
import com.silsonfit.silsonfit_api.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 관련 API
 *
 * 카카오 로그인과 토큰 재발급 제공
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 카카오 로그인
     *
     * 클라이언트가 카카오 SDK로 받은 Access Token 전달 시
     * 서비스의 Access/Refresh Token 발급
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    /**
     * 토큰 재발급 (Refresh Token Rotation)
     */
    @PostMapping("/reissue")
    public ApiResponse<TokenReissueResponse> reissue(
            @Valid @RequestBody TokenReissueRequest request) {
        return ApiResponse.success(authService.reissue(request));
    }
}
