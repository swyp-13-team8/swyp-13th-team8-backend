package com.silsonfit.silsonfit_api.domain.calculation.service;

import com.silsonfit.silsonfit_api.domain.calculation.entity.EdiCode;
import com.silsonfit.silsonfit_api.domain.calculation.policy.CoverageRuleGenerationPolicy;
import com.silsonfit.silsonfit_api.domain.calculation.vo.CoverageRuleContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 보장 룰 생성 정책 Resolver
 *
 * - 보험 세대, 보험 ID, EDI 코드 조건을 만족하는 정책을 선택한다.
 * - 구체적인 정책을 높은 우선순위로 등록하면 기본 세대별 정책보다 먼저 적용된다.
 */
@Component
@RequiredArgsConstructor
public class CoverageRulePolicyResolver {

    private final List<CoverageRuleGenerationPolicy> policies;

    /**
     * 보장 룰 생성 정책 조회
     *
     * @param context 보험 조건
     * @param ediCode EDI 코드
     * @return 적용할 보장 룰 생성 정책
     */
    public CoverageRuleGenerationPolicy resolve(CoverageRuleContext context, EdiCode ediCode) {
        return policies.stream()
                .filter(policy -> policy.supports(context, ediCode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("적용 가능한 보장 룰 생성 정책이 없습니다: " + context.generation()));
    }
}
