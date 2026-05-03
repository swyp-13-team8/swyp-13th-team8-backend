package com.silsonfit.silsonfit_api.domain.calculation.policy;

import com.silsonfit.silsonfit_api.domain.calculation.enums.InsuranceGeneration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 1세대 실손보험 보장 룰 생성 정책
 */
@Component
@Order(100)
public class FirstGenerationCoverageRuleGenerationPolicy extends AbstractCoverageRuleGenerationPolicy {

    public FirstGenerationCoverageRuleGenerationPolicy() {
        super(
                InsuranceGeneration.FIRST,
                100,
                0,
                100,
                0,
                "1세대 실손보험은 급여/비급여 대부분을 보장하며 자기부담금이 거의 없는 한도 중심 임시 정책 기준입니다."
        );
    }
}
