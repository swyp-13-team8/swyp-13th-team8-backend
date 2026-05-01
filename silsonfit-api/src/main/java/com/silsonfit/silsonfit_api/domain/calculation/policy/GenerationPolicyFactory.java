package com.silsonfit.silsonfit_api.domain.calculation.policy;

import com.silsonfit.silsonfit_api.domain.calculation.enums.InsuranceGeneration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 보험 세대별 계산 Policy Factory
 *
 */
@Component
@RequiredArgsConstructor
public class GenerationPolicyFactory {

    private final List<InsuranceGenerationPolicy> policies;

    /**
     * 보험 세대에 맞는 계산 Policy 반환
     *
     * @param generation 보험 세대
     * @return 해당 세대를 처리할 Policy
     */
    public InsuranceGenerationPolicy getPolicy(InsuranceGeneration generation) {
        return policies.stream()
                .filter(policy -> policy.supports(generation))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("지원하지 않는 보험 세대입니다: " + generation)
                );
    }
}