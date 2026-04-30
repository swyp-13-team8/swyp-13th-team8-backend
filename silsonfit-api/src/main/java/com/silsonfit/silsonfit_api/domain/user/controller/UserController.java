package com.silsonfit.silsonfit_api.domain.user.controller;

import com.silsonfit.silsonfit_api.domain.user.dto.UserResponse;
import com.silsonfit.silsonfit_api.domain.user.dto.UserUpdateRequest;
import com.silsonfit.silsonfit_api.domain.user.service.UserService;
import com.silsonfit.silsonfit_api.global.auth.CustomUserDetails;
import com.silsonfit.silsonfit_api.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 프로필 API
 *
 * 내 정보 조회 및 닉네임 수정 제공
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 내 프로필 조회
     */
    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(userService.getMyProfile(userDetails.getUserId()));
    }

    /**
     * 닉네임 수정
     */
    @PatchMapping("/me")
    public ApiResponse<Void> updateMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserUpdateRequest request) {
        userService.updateMyProfile(userDetails.getUserId(), request);
        return ApiResponse.success();
    }
}
