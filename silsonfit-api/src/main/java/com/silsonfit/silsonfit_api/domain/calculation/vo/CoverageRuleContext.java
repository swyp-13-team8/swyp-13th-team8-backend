package com.silsonfit.silsonfit_api.domain.calculation.vo;

import com.silsonfit.silsonfit_api.domain.calculation.enums.InsuranceGeneration;

/**
 * 보장 룰 조회/생성에 필요한 보험 조건
 *
 * - 보험 도메인 연동 전까지 계산 도메인에서 필요한 최소 보험 정보를 담는다.
 * - 이후 보험 엔티티/약관 정보가 추가되면 이 Context를 확장하거나 대체한다.
 */
public record CoverageRuleContext(
        Long insuranceId,
        InsuranceGeneration generation
) {
}
