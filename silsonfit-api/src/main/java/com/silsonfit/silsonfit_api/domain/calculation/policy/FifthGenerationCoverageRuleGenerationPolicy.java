package com.silsonfit.silsonfit_api.domain.calculation.policy;

import com.silsonfit.silsonfit_api.domain.calculation.enums.InsuranceGeneration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 5세대 실손보험 보장 룰 생성 정책
 */
@Component
@Order(100)
public class FifthGenerationCoverageRuleGenerationPolicy extends AbstractCoverageRuleGenerationPolicy {

    public FifthGenerationCoverageRuleGenerationPolicy() {
        super(
                InsuranceGeneration.FIFTH,
                70,
                10000,
                60,
                30000,
                "5세대 실손보험 임시 정책 기준입니다."
        );
    }
}
