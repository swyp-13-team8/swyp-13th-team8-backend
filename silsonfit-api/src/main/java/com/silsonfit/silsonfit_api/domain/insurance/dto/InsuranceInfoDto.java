package com.silsonfit.silsonfit_api.domain.insurance.dto;

import com.silsonfit.silsonfit_api.domain.insurance.enums.CautionPoint;
import com.silsonfit.silsonfit_api.domain.insurance.enums.ContractType;
import com.silsonfit.silsonfit_api.domain.insurance.enums.CoverageStructure;

/**
 * 타 도메인(계산/분석)에 보험 정보를 제공하는 DTO
 *
 * Insurance(보험 상품) + UserInsurance(사용자 등록 정보)를 함께 제공
 */
public record InsuranceInfoDto(
        Long insuranceId,
        String companyName,
        String productName,
        ContractType contractType,
        int generation,
        CoverageStructure coverageStructure,
        CautionPoint cautionPoint,
        String pdfFileUrl,
        String pdfFileName,
        String subscribedAt,
        boolean hasNonCoveredRider
) {
}
