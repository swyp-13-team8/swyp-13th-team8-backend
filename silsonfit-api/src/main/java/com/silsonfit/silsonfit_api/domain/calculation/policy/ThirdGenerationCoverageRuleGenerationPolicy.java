package com.silsonfit.silsonfit_api.domain.calculation.policy;

import com.silsonfit.silsonfit_api.domain.calculation.entity.EdiCode;
import com.silsonfit.silsonfit_api.domain.calculation.enums.InsuranceGeneration;
import com.silsonfit.silsonfit_api.domain.calculation.vo.CoverageRuleContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 3세대 실손보험 보장 룰 생성 정책
 *
 * - 급여는 기본계약, 비급여는 특약으로 분리된 구조
 * - 비급여는 특약 가입 여부와 항목별 제한을 확인해야 하지만, 현재는 특약 가입을 전제로 한 임시 기준을 적용한다.
 */
@Component
@Order(100)
public class ThirdGenerationCoverageRuleGenerationPolicy extends AbstractCoverageRuleGenerationPolicy {

    public ThirdGenerationCoverageRuleGenerationPolicy() {
        super(
                InsuranceGeneration.THIRD,
                80,
                10000,
                70,
                30000,
                "3세대 실손보험은 급여 기본계약과 비급여 특약을 분리하며, 현재는 비급여 특약 가입을 전제로 한 임시 정책 기준입니다."
        );
    }

    @Override
    public String disclaimer(CoverageRuleContext context, EdiCode ediCode) {
        if (!ediCode.isPay()) {
            return "3세대 실손보험 비급여는 특약 분리 항목으로, 특약 가입 및 항목별 한도 확인이 필요한 임시 정책 기준입니다.";
        }

        return super.disclaimer(context, ediCode);
    }
}
