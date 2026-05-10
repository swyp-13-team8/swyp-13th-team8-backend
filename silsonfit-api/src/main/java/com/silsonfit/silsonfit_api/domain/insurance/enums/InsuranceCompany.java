package com.silsonfit.silsonfit_api.domain.insurance.enums;

import com.silsonfit.silsonfit_api.global.error.BusinessException;
import com.silsonfit.silsonfit_api.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 보험사 enum
 *
 * API 명세서 기준 보험사 ID와 표시명을 매핑한다.
 */
@Getter
@AllArgsConstructor
public enum InsuranceCompany {

    SAMSUNG_FIRE("comp_001", "삼성화재"),
    HYUNDAI_MARINE("comp_002", "현대해상"),
    DB_INSURANCE("comp_003", "DB손해보험"),
    KB_INSURANCE("comp_004", "KB손해보험"),
    MERITZ_FIRE("comp_005", "메리츠화재"),
    ETC("comp_006", "기타");

    private final String id;
    private final String displayName;

    /**
     * 보험사 ID로 enum 조회
     *
     * @param companyId 보험사 ID (예: "comp_001")
     * @return 해당 보험사 enum
     */
    public static InsuranceCompany fromId(String companyId) {
        for (InsuranceCompany company : values()) {
            if (company.id.equals(companyId)) {
                return company;
            }
        }
        throw new BusinessException(ErrorCode.INSURANCE_COMPANY_NOT_FOUND);
    }

    /**
     * 보험사명으로 enum 조회
     *
     * @param displayName 보험사명 (예: "삼성화재")
     * @return 해당 보험사 enum
     */
    public static InsuranceCompany fromDisplayName(String displayName) {
        for (InsuranceCompany company : values()) {
            if (company.displayName.equals(displayName)) {
                return company;
            }
        }
        throw new BusinessException(ErrorCode.INSURANCE_COMPANY_NOT_FOUND);
    }

    /**
     * 빅5 보험사 여부
     */
    public boolean isBigFive() {
        return this != ETC;
    }
}
