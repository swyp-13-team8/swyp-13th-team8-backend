package com.silsonfit.silsonfit_api.domain.calculation.policy;

import com.silsonfit.silsonfit_api.domain.calculation.enums.InsuranceGeneration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 2세대 실손보험 보장 룰 생성 정책
 *
 * - 자기부담금이 도입된 표준화 실손 구조
 * - 급여는 약 10%, 비급여는 약 20% 수준의 자기부담을 적용한다.
 */
@Component
@Order(100)
public class SecondGenerationCoverageRuleGenerationPolicy extends AbstractCoverageRuleGenerationPolicy {

    public SecondGenerationCoverageRuleGenerationPolicy() {
        super(
                InsuranceGeneration.SECOND,
                90,
                10000,
                80,
                20000,
                "2세대 실손보험은 표준화 이후 급여/비급여 보장을 유지하되 자기부담 10~20%를 적용하는 임시 정책 기준입니다."
        );
    }
}
