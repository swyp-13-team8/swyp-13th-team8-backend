package com.silsonfit.silsonfit_api.domain.insurance.service;

import com.silsonfit.silsonfit_api.domain.insurance.dto.GenerationRequest;
import com.silsonfit.silsonfit_api.domain.insurance.dto.GenerationResponse;
import com.silsonfit.silsonfit_api.domain.insurance.dto.InsuranceCompanyResponse;
import com.silsonfit.silsonfit_api.domain.insurance.dto.InsuranceInfoDto;
import com.silsonfit.silsonfit_api.domain.insurance.dto.InsuranceProductResponse;
import com.silsonfit.silsonfit_api.domain.insurance.dto.InsuranceRegisterRequest;
import com.silsonfit.silsonfit_api.domain.insurance.dto.InsuranceRegisterResponse;
import com.silsonfit.silsonfit_api.domain.insurance.dto.UserInsuranceResponse;
import com.silsonfit.silsonfit_api.domain.insurance.entity.Insurance;
import com.silsonfit.silsonfit_api.domain.insurance.enums.InsuranceCompany;
import com.silsonfit.silsonfit_api.domain.insurance.enums.InsuranceGeneration;
import com.silsonfit.silsonfit_api.domain.insurance.entity.UserInsurance;
import com.silsonfit.silsonfit_api.domain.insurance.repository.InsuranceRepository;
import com.silsonfit.silsonfit_api.domain.insurance.repository.UserInsuranceRepository;
import com.silsonfit.silsonfit_api.global.error.BusinessException;
import com.silsonfit.silsonfit_api.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 보험 관련 비즈니스 로직
 *
 * 세대 판별, 보험 등록/삭제, 상품 목록 조회, 타 도메인 보험 정보 제공
 */
@Service
@RequiredArgsConstructor
public class InsuranceService {

    private static final int MAX_INSURANCE_COUNT = 5;

    private final InsuranceRepository insuranceRepository;
    private final UserInsuranceRepository userInsuranceRepository;

    /**
     * 가입 연월 기반 세대 판별
     *
     * @param request 보험사 ID + 가입 연월
     * @return 세대 번호
     */
    public GenerationResponse determineGeneration(GenerationRequest request) {
        // 보험사 ID 유효성 검증
        InsuranceCompany.fromId(request.companyId());

        InsuranceGeneration gen = InsuranceGeneration.from(request.joinDate());
        return new GenerationResponse(gen.getGeneration());
    }

    /**
     * 보험사 목록 조회
     *
     * @return 빅5 보험사 + 기타 목록
     */
    public List<InsuranceCompanyResponse> getCompanies() {
        return Arrays.stream(InsuranceCompany.values())
                .map(company -> new InsuranceCompanyResponse(company.getId(), company.getDisplayName()))
                .toList();
    }

    /**
     * 보험 등록
     *
     * @param userId 사용자 ID
     * @param request 보험 등록 요청
     * @return 등록된 사용자 보험 ID
     */
    @Transactional
    public InsuranceRegisterResponse register(Long userId, InsuranceRegisterRequest request) {
        // 최대 5개 제한 검증
        long count = userInsuranceRepository.countByUserId(userId);
        if (count >= MAX_INSURANCE_COUNT) {
            throw new BusinessException(ErrorCode.INSURANCE_LIMIT_EXCEEDED);
        }

        // 동일 보험 상품 중복 등록 검증
        if (userInsuranceRepository.existsByUserIdAndInsuranceId(userId, request.insuranceId())) {
            throw new BusinessException(ErrorCode.INSURANCE_ALREADY_REGISTERED);
        }

        // 보험 상품 존재 여부 검증
        Insurance insurance = insuranceRepository.findById(request.insuranceId())
                .orElseThrow(() -> new BusinessException(ErrorCode.INSURANCE_NOT_FOUND));

        UserInsurance userInsurance = UserInsurance.builder()
                .userId(userId)
                .insurance(insurance)
                .subscribedAt(request.subscribedAt())
                .hasNonCoveredRider(false)
                .build();

        UserInsurance saved = userInsuranceRepository.save(userInsurance);
        return new InsuranceRegisterResponse(saved.getId());
    }

    // NOTE: 기능 명세에 보험 삭제 기능이 없어 주석 처리. 추후 필요 시 해제.
    /*
    @Transactional
    public void delete(Long userId, Long userInsuranceId) {
        UserInsurance userInsurance = userInsuranceRepository.findById(userInsuranceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INSURANCE_NOT_FOUND));

        if (!userInsurance.isOwnedBy(userId)) {
            throw new BusinessException(ErrorCode.USER_INSURANCE_ACCESS_DENIED);
        }

        userInsuranceRepository.delete(userInsurance);
    }
    */

    /**
     * 보험사별 상품 목록 조회
     *
     * @param companyId 보험사 ID (예: "comp_001")
     * @param generation 세대
     * @return 상품 목록
     */
    @Transactional(readOnly = true)
    public List<InsuranceProductResponse> getProducts(String companyId, int generation) {
        InsuranceCompany company = InsuranceCompany.fromId(companyId);
        List<Insurance> products = insuranceRepository.findByCompanyNameAndGeneration(
                company.getDisplayName(), generation);

        return products.stream()
                .map(insurance -> new InsuranceProductResponse(
                        insurance.getId(),
                        insurance.getProductName()
                ))
                .toList();
    }

    /**
     * 사용자 등록 보험 목록 조회
     *
     * @param userId 사용자 ID
     * @return 등록 보험 목록
     */
    @Transactional(readOnly = true)
    public List<UserInsuranceResponse> getMyInsurances(Long userId) {
        List<UserInsurance> userInsurances = userInsuranceRepository.findByUserIdWithInsurance(userId);

        return userInsurances.stream()
                .map(ui -> new UserInsuranceResponse(
                        ui.getId(), // 보험 등록 건의 고유 번호
                        ui.getInsurance().getCompanyName(),
                        ui.getInsurance().getProductName(),
                        ui.getInsurance().getGeneration(),
                        ui.getSubscribedAt()
                ))
                .toList();
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
