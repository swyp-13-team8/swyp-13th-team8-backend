package com.silsonfit.silsonfit_api.domain.calculation.policy;

import com.silsonfit.silsonfit_api.domain.calculation.entity.EdiCode;
import com.silsonfit.silsonfit_api.domain.calculation.enums.InsuranceGeneration;
import com.silsonfit.silsonfit_api.domain.calculation.vo.CoverageRuleContext;

/**
 * 세대별 보장 룰 생성 정책 기본 구현
 */
public abstract class AbstractCoverageRuleGenerationPolicy implements CoverageRuleGenerationPolicy {

    private final InsuranceGeneration generation;
    private final int payCoverageRate;
    private final int payDeductibleAmount;
    private final int nonPayCoverageRate;
    private final int nonPayDeductibleAmount;
    private final String disclaimer;

    protected AbstractCoverageRuleGenerationPolicy(
            InsuranceGeneration generation,
            int payCoverageRate,
            int payDeductibleAmount,
            int nonPayCoverageRate,
            int nonPayDeductibleAmount,
            String disclaimer
    ) {
        this.generation = generation;
        this.payCoverageRate = payCoverageRate;
        this.payDeductibleAmount = payDeductibleAmount;
        this.nonPayCoverageRate = nonPayCoverageRate;
        this.nonPayDeductibleAmount = nonPayDeductibleAmount;
        this.disclaimer = disclaimer;
    }

    @Override
    public boolean supports(CoverageRuleContext context, EdiCode ediCode) {
        return context.generation() == generation;
    }

    @Override
    public boolean isCovered(CoverageRuleContext context, EdiCode ediCode) {
        if (ediCode.containsKeyword("건강검진") || ediCode.containsKeyword("검진")) {
            return false;
        }
        if (ediCode.containsKeyword("미용") || ediCode.containsKeyword("성형")) {
            return false;
        }
        if (ediCode.containsKeyword("예방") || ediCode.containsKeyword("예방접종")) {
            return false;
        }

        return true;
    }

    @Override
    public int coverageRate(CoverageRuleContext context, EdiCode ediCode) {
        if (!isCovered(context, ediCode)) {
            return 0;
        }

        if (ediCode.isPay()) {
            return payCoverageRate;
        }

        return nonPayCoverageRate;
    }

    @Override
    public int deductibleAmount(CoverageRuleContext context, EdiCode ediCode) {
        if (!isCovered(context, ediCode)) {
            return 0;
        }

        if (ediCode.isPay()) {
            return payDeductibleAmount;
        }

        return nonPayDeductibleAmount;
    }

    @Override
    public Integer limitAmount(CoverageRuleContext context, EdiCode ediCode) {
        if (ediCode.getUnitPrice() == null || ediCode.getUnitPrice() <= 0) {
            return null;
        }

        return ediCode.getUnitPrice();
    }

    @Override
    public String disclaimer(CoverageRuleContext context, EdiCode ediCode) {
        return disclaimer;
    }
}
