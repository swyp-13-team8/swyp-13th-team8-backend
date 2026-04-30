package com.silsonfit.silsonfit_api.domain.calculation.controller;

import com.silsonfit.silsonfit_api.domain.calculation.dto.CalculationRequest;
import com.silsonfit.silsonfit_api.domain.calculation.dto.CalculationResponse;
import com.silsonfit.silsonfit_api.domain.calculation.service.CalculationService;
import com.silsonfit.silsonfit_api.global.auth.CustomUserDetails;
import com.silsonfit.silsonfit_api.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 실손 보험 계산 API
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/calculations")
public class CalculationController {

    private final CalculationService calculationService;

    /**
     * 실손 보험 예상 환급액 계산
     *
     * @param request 계산 요청
     * @return 계산 결과
     */
    @PostMapping
    public ApiResponse<CalculationResponse> calculate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CalculationRequest request
    ) {
        Long userId = userDetails.getUserId();

        log.info(
                "실손 보험 계산 요청 - userId={}, insuranceId={}, medicalCost={}, ediCode={}",
                userId,
                request.getInsuranceId(),
                request.getMedicalCost(),
                request.getEdiCode()
        );

        return ApiResponse.success(calculationService.calculate(userId, request));
    }
}
