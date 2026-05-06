package com.silsonfit.silsonfit_api.domain.insurance.controller;

import com.silsonfit.silsonfit_api.domain.insurance.dto.GenerationRequest;
import com.silsonfit.silsonfit_api.domain.insurance.dto.GenerationResponse;
import com.silsonfit.silsonfit_api.domain.insurance.dto.InsuranceCompanyResponse;
import com.silsonfit.silsonfit_api.domain.insurance.dto.InsuranceDetailResponse;
import com.silsonfit.silsonfit_api.domain.insurance.dto.InsuranceProductResponse;
import com.silsonfit.silsonfit_api.domain.insurance.dto.InsuranceRegisterRequest;
import com.silsonfit.silsonfit_api.domain.insurance.dto.InsuranceRegisterResponse;
import com.silsonfit.silsonfit_api.domain.insurance.dto.UserInsuranceResponse;
import com.silsonfit.silsonfit_api.domain.insurance.service.InsuranceService;
import com.silsonfit.silsonfit_api.global.auth.CustomUserDetails;
import com.silsonfit.silsonfit_api.global.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 보험 관련 API
 *
 * 세대 판별, 보험사 목록, 보험 등록, 내 보험 목록, 상품 목록 조회 제공
 */
@RestController
@RequestMapping("/api/insurance")
@RequiredArgsConstructor
public class InsuranceController {

    private final InsuranceService insuranceService;

    /**
     * 보험사 목록 조회
     */
    @GetMapping("/companies")
    public ApiResponse<Map<String, List<InsuranceCompanyResponse>>> getCompanies() {
        return ApiResponse.success(Map.of("companies", insuranceService.getCompanies()));
    }

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

    // NOTE: 기능 명세에 보험 삭제 기능이 없어 주석 처리. 추후 필요 시 해제.
    /*
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Long userInsuranceId) {
        insuranceService.delete(userDetails.getUserId(), userInsuranceId);
        return ApiResponse.success();
    }
    */

    /**
     * 등록 보험 상세 조회
     */
    @GetMapping("/{id}")
    public ApiResponse<InsuranceDetailResponse> getInsuranceDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Long userInsuranceId) {
        return ApiResponse.success(insuranceService.getInsuranceDetail(userDetails.getUserId(), userInsuranceId));
    }

    /**
     * 내 보험 목록 조회
     */
    @GetMapping("/list")
    public ApiResponse<Map<String, List<UserInsuranceResponse>>> getMyInsurances(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(Map.of("insurances", insuranceService.getMyInsurances(userDetails.getUserId())));
    }

    /**
     * 보험사별 상품 매칭 조회
     */
    @GetMapping("/products")
    public ApiResponse<Map<String, List<InsuranceProductResponse>>> getProducts(
            @RequestParam String companyName,
            @RequestParam int generation) {
        return ApiResponse.success(Map.of("products", insuranceService.getProducts(companyName, generation)));
    }
}
