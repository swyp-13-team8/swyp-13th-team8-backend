package com.silsonfit.silsonfit_api.domain.insurance.controller;

import com.silsonfit.silsonfit_api.domain.insurance.dto.GenerationRequest;
import com.silsonfit.silsonfit_api.domain.insurance.dto.GenerationResponse;
import com.silsonfit.silsonfit_api.domain.insurance.service.InsuranceService;
import com.silsonfit.silsonfit_api.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 보험 관련 API
 *
 * 세대 판별 API 제공
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
}
