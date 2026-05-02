package com.silsonfit.silsonfit_api.domain.insurance.service;

import com.silsonfit.silsonfit_api.domain.insurance.dto.GenerationRequest;
import com.silsonfit.silsonfit_api.domain.insurance.dto.GenerationResponse;
import com.silsonfit.silsonfit_api.domain.insurance.dto.InsuranceInfoDto;
import com.silsonfit.silsonfit_api.domain.insurance.entity.Insurance;
import com.silsonfit.silsonfit_api.domain.insurance.enums.InsuranceGeneration;
import com.silsonfit.silsonfit_api.domain.insurance.entity.UserInsurance;
import com.silsonfit.silsonfit_api.domain.insurance.repository.InsuranceRepository;
import com.silsonfit.silsonfit_api.domain.insurance.repository.UserInsuranceRepository;
import com.silsonfit.silsonfit_api.global.error.BusinessException;
import com.silsonfit.silsonfit_api.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 보험 관련 비즈니스 로직
 *
 * 세대 판별, 타 도메인 보험 정보 제공
 */
@Service
@RequiredArgsConstructor
public class InsuranceService {

    private final InsuranceRepository insuranceRepository;
    private final UserInsuranceRepository userInsuranceRepository;

    /**
     * 가입 연월 기반 세대 판별
     *
     * @param request 가입 연월
     * @return 세대 번호 및 설명
     */
    public GenerationResponse determineGeneration(GenerationRequest request) {
        InsuranceGeneration gen = InsuranceGeneration.from(request.subscribedYearMonth());
        return new GenerationResponse(gen.getGeneration(), gen.getDescription());
    }

    /**
     * 타 도메인(계산/분석)에서 사용할 보험 정보 조회
     *
     * @param userInsuranceId 사용자 보험 등록 ID
     * @return 보험 상품 정보 DTO
     */
    @Transactional(readOnly = true)
    public InsuranceInfoDto getInsuranceInfo(Long userInsuranceId) {
        UserInsurance userInsurance = userInsuranceRepository.findById(userInsuranceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INSURANCE_NOT_FOUND));

        Insurance insurance = userInsurance.getInsurance();

        return new InsuranceInfoDto(
                insurance.getId(),
                insurance.getCompanyName(),
                insurance.getProductName(),
                insurance.getContractType(),
                insurance.getGeneration(),
                insurance.getCoverageStructure(),
                insurance.getCautionPoint(),
                insurance.getPdfFileUrl(),
                insurance.getPdfFileName(),
                userInsurance.getSubscribedAt(),
                userInsurance.isHasNonCoveredRider()
        );
    }

    /**
     * 보험 ID로 세대 정보 조회
     *
     * @param insuranceId 보험 상품 ID
     * @return 세대 번호
     */
    @Transactional(readOnly = true)
    public int getGenerationByInsuranceId(Long insuranceId) {
        Insurance insurance = insuranceRepository.findById(insuranceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INSURANCE_NOT_FOUND));

        return insurance.getGeneration();
    }
}
