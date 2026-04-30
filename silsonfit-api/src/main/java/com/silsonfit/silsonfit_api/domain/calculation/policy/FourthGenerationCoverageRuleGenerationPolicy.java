package com.silsonfit.silsonfit_api.domain.calculation.policy;

import com.silsonfit.silsonfit_api.domain.calculation.entity.EdiCode;
import com.silsonfit.silsonfit_api.domain.calculation.enums.InsuranceGeneration;
import com.silsonfit.silsonfit_api.domain.calculation.vo.CoverageRuleContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 4세대 실손보험 보장 룰 생성 정책
 *
 * - 급여 기본계약 + 비급여 특약 구조를 유지한다.
 * - 비급여 이용량을 기반으로 보험료를 차등 적용하는 행동 기반 구조다.
 * TODO(calculation): UsageHistory/AnnualAggregation/PremiumAdjustment 도메인 추가 후
 *  annualNonCoveredUsage 기반 보험료 할인/유지/할증 정책을 연결한다.
 */
@Component
@Order(100)
public class FourthGenerationCoverageRuleGenerationPolicy extends AbstractCoverageRuleGenerationPolicy {

    public FourthGenerationCoverageRuleGenerationPolicy() {
        super(
                InsuranceGeneration.FOURTH,
                80,
                10000,
                70,
                30000,
                "4세대 실손보험은 급여 20%, 비급여 30% 수준의 자기부담과 비급여 이용량 기반 보험료 차등을 전제로 한 임시 정책 기준입니다."
        );
    }

    @Override
    public String disclaimer(CoverageRuleContext context, EdiCode ediCode) {
        if (!ediCode.isPay()) {
            return "4세대 실손보험 비급여는 특약 분리 및 이용량 기반 보험료 차등 대상이며, 연간 비급여 사용량 집계가 필요한 임시 정책 기준입니다.";
        }

        return super.disclaimer(context, ediCode);
    }
}
