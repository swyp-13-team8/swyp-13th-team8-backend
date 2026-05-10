package com.silsonfit.silsonfit_api.domain.calculation.policy;

import com.silsonfit.silsonfit_api.domain.calculation.entity.EdiCode;
import com.silsonfit.silsonfit_api.domain.calculation.vo.CoverageRuleContext;

/**
 * 보장 룰 생성 정책
 *
 * - 보험 세대, 보험 ID, EDI 코드 정보에 따라 CoverageRule 생성 조건을 결정한다.
 * - 기본 세대별 정책보다 구체적인 보험 ID별 정책을 더 높은 우선순위로 추가할 수 있다.
 */
public interface CoverageRuleGenerationPolicy {

    /**
     * 해당 정책 적용 가능 여부
     */
    boolean supports(CoverageRuleContext context, EdiCode ediCode);

    /**
     * 보장 여부 결정
     */
    boolean isCovered(CoverageRuleContext context, EdiCode ediCode);

    /**
     * 보장률 결정
     */
    int coverageRate(CoverageRuleContext context, EdiCode ediCode);

    /**
     * 자기부담금 결정
     */
    int deductibleAmount(CoverageRuleContext context, EdiCode ediCode);

    /**
     * 보장 한도 결정
     */
    Integer limitAmount(CoverageRuleContext context, EdiCode ediCode);

    /**
     * 면책/주의 문구
     */
    String disclaimer(CoverageRuleContext context, EdiCode ediCode);
}
