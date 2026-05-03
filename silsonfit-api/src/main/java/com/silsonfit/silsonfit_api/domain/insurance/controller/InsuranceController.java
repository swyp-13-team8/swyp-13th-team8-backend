package com.silsonfit.silsonfit_api.domain.insurance.controller;

import com.silsonfit.silsonfit_api.domain.insurance.dto.GenerationRequest;
import com.silsonfit.silsonfit_api.domain.insurance.dto.GenerationResponse;
import com.silsonfit.silsonfit_api.domain.insurance.dto.InsuranceRegisterRequest;
import com.silsonfit.silsonfit_api.domain.insurance.dto.InsuranceRegisterResponse;
import com.silsonfit.silsonfit_api.domain.insurance.service.InsuranceService;
import com.silsonfit.silsonfit_api.global.auth.CustomUserDetails;
import com.silsonfit.silsonfit_api.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 보험 관련 API
 *
 * 세대 판별, 보험 등록 제공
 */
@RestController
@RequestMapping("/api/insurance")
@RequiredArgsConstructor
public class InsuranceController {

    private final InsuranceService insuranceService;

    /**
     * 가입 연월 기반 세대 판별
     */
    @PostMapping("/generation")
    public ApiResponse<GenerationResponse> determineGeneration(
            @Valid @RequestBody GenerationRequest request) {
        return ApiResponse.success(insuranceService.determineGeneration(request));
    }

    /**
     * 보험 등록
     */
    @PostMapping("/register")
    public ApiResponse<InsuranceRegisterResponse> register(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody InsuranceRegisterRequest request) {
        return ApiResponse.success(insuranceService.register(userDetails.getUserId(), request));
    }
}
