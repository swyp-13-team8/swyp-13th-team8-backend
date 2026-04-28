package com.silsonfit.silsonfit_api.domain.auth.dto;

import jakarta.validation.constraints.AssertTrue;

/**
 * 약관 동의 요청 DTO
 */
public record TermsAgreeRequest(
        @AssertTrue(message = "만 14세 이상이어야 합니다.")
        boolean ageOver14,

        @AssertTrue(message = "서비스 이용약관에 동의해야 합니다.")
        boolean serviceTerms,

        @AssertTrue(message = "개인정보 수집 및 이용에 동의해야 합니다.")
        boolean privacyPolicy
) {
}
