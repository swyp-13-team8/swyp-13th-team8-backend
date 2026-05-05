package com.silsonfit.silsonfit_api.domain.calculation.service;

import com.silsonfit.silsonfit_api.domain.calculation.entity.CoverageRule;
import com.silsonfit.silsonfit_api.domain.calculation.entity.EdiCode;
import com.silsonfit.silsonfit_api.domain.calculation.enums.FeeType;
import com.silsonfit.silsonfit_api.domain.calculation.enums.InsuranceGeneration;
import com.silsonfit.silsonfit_api.domain.calculation.enums.PayType;
import com.silsonfit.silsonfit_api.domain.calculation.enums.TreatmentCategory;
import com.silsonfit.silsonfit_api.domain.calculation.enums.VisitType;
import com.silsonfit.silsonfit_api.domain.calculation.policy.FifthGenerationCoverageRuleGenerationPolicy;
import com.silsonfit.silsonfit_api.domain.calculation.policy.FirstGenerationCoverageRuleGenerationPolicy;
import com.silsonfit.silsonfit_api.domain.calculation.policy.FourthGenerationCoverageRuleGenerationPolicy;
import com.silsonfit.silsonfit_api.domain.calculation.policy.SecondGenerationCoverageRuleGenerationPolicy;
import com.silsonfit.silsonfit_api.domain.calculation.policy.ThirdGenerationCoverageRuleGenerationPolicy;
import com.silsonfit.silsonfit_api.domain.calculation.vo.CoverageRuleContext;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CoverageRuleGeneratorTest {

    CoverageRuleGenerator coverageRuleGenerator = new CoverageRuleGenerator(
            new CoverageRulePolicyResolver(List.of(
                    new FirstGenerationCoverageRuleGenerationPolicy(),
                    new SecondGenerationCoverageRuleGenerationPolicy(),
                    new ThirdGenerationCoverageRuleGenerationPolicy(),
                    new FourthGenerationCoverageRuleGenerationPolicy(),
                    new FifthGenerationCoverageRuleGenerationPolicy()
            ))
    );
    CoverageRuleContext context = new CoverageRuleContext(1L, InsuranceGeneration.FOURTH);

    @Test
    void 급여_EDI코드는_급여_기본_보장룰을_생성한다() {
        EdiCode ediCode = createEdiCode("EDI001", "MRI 검사", "MRI", PayType.PAY, FeeType.MEDICAL);

        CoverageRule coverageRule = coverageRuleGenerator.generate(
                context,
                ediCode
        );

        assertThat(coverageRule.getEdiCode()).isEqualTo("EDI001");
        assertThat(coverageRule.getVisitType()).isEqualTo(VisitType.OUTPATIENT);
        assertThat(coverageRule.getTreatmentCategory()).isEqualTo(TreatmentCategory.MRI);
        assertThat(coverageRule.getCoverageRate()).isEqualTo(80);
        assertThat(coverageRule.getDeductibleAmount()).isEqualTo(10000);
        assertThat(coverageRule.getLimitAmount()).isEqualTo(100000);
        assertThat(coverageRule.getIsCovered()).isTrue();
        assertThat(coverageRule.getBasis()).hasSize(2);
        assertThat(coverageRule.getBasis().get(0)).contains("4세대", "보장");
        assertThat(coverageRule.getBasis().get(1))
                .contains("EDI001", "MRI 검사", "MRI", "급여", "진료수가", "100000", "123.45", "2026-01-01");
    }

    @Test
    void 비급여_EDI코드는_비급여_기본_보장룰을_생성한다() {
        EdiCode ediCode = createEdiCode("EDI002", "도수치료", "도수", PayType.NON_PAY, FeeType.MEDICAL);

        CoverageRule coverageRule = coverageRuleGenerator.generate(
                context,
                ediCode
        );

        assertThat(coverageRule.getEdiCode()).isEqualTo("EDI002");
        assertThat(coverageRule.getTreatmentCategory()).isEqualTo(TreatmentCategory.MANUAL_THERAPY);
        assertThat(coverageRule.getCoverageRate()).isEqualTo(70);
        assertThat(coverageRule.getDeductibleAmount()).isEqualTo(30000);
        assertThat(coverageRule.getLimitAmount()).isEqualTo(100000);
        assertThat(coverageRule.getBasis()).hasSize(2);
        assertThat(coverageRule.getBasis().get(1)).contains("EDI002", "도수치료", "도수", "비급여", "진료수가");
    }

    @Test
    void 약국_EDI코드는_약제_진료유형으로_생성한다() {
        EdiCode ediCode = createEdiCode("EDI003", "처방 조제", "약국", PayType.PAY, FeeType.PHARMACY);

        CoverageRule coverageRule = coverageRuleGenerator.generate(
                context,
                ediCode
        );

        assertThat(coverageRule.getVisitType()).isEqualTo(VisitType.MEDICATION);
        assertThat(coverageRule.getTreatmentCategory()).isEqualTo(TreatmentCategory.GENERAL_TREATMENT);
    }

    @Test
    void 검진_EDI코드는_비보장_보장룰을_생성한다() {
        EdiCode ediCode = createEdiCode("EDI004", "건강검진 검사", "검진", PayType.PAY, FeeType.MEDICAL);

        CoverageRule coverageRule = coverageRuleGenerator.generate(
                context,
                ediCode
        );

        assertThat(coverageRule.getIsCovered()).isFalse();
        assertThat(coverageRule.getCoverageRate()).isZero();
        assertThat(coverageRule.getDeductibleAmount()).isZero();
        assertThat(coverageRule.getBasis()).hasSize(2);
        assertThat(coverageRule.getBasis().get(0)).contains("비보장");
        assertThat(coverageRule.getBasis().get(1)).contains("건강검진 검사");
    }

    @Test
    void 보험_세대별_정책값을_다르게_적용한다() {
        EdiCode ediCode = createEdiCode("EDI005", "일반 진료", "일반", PayType.NON_PAY, FeeType.MEDICAL);
        CoverageRuleContext firstGenerationContext =
                new CoverageRuleContext(1L, InsuranceGeneration.FIRST);
        CoverageRuleContext fifthGenerationContext =
                new CoverageRuleContext(1L, InsuranceGeneration.FIFTH);

        CoverageRule firstGenerationRule = coverageRuleGenerator.generate(firstGenerationContext, ediCode);
        CoverageRule fifthGenerationRule = coverageRuleGenerator.generate(fifthGenerationContext, ediCode);

        assertThat(firstGenerationRule.getCoverageRate()).isEqualTo(100);
        assertThat(firstGenerationRule.getDeductibleAmount()).isZero();
        assertThat(firstGenerationRule.getDisclaimer()).contains("1세대", "급여/비급여 대부분", "자기부담금이 거의 없는", "한도 중심");
        assertThat(fifthGenerationRule.getCoverageRate()).isEqualTo(60);
        assertThat(fifthGenerationRule.getDeductibleAmount()).isEqualTo(30000);
        assertThat(fifthGenerationRule.getDisclaimer()).contains("5세대");
    }

    @Test
    void 이세대는_급여_10퍼센트_비급여_20퍼센트_자기부담_구조를_적용한다() {
        EdiCode payEdiCode = createEdiCode("EDI006", "외래 진료", "일반", PayType.PAY, FeeType.MEDICAL);
        EdiCode nonPayEdiCode = createEdiCode("EDI007", "비급여 도수치료", "도수", PayType.NON_PAY, FeeType.MEDICAL);
        CoverageRuleContext secondGenerationContext =
                new CoverageRuleContext(1L, InsuranceGeneration.SECOND);

        CoverageRule payRule = coverageRuleGenerator.generate(secondGenerationContext, payEdiCode);
        CoverageRule nonPayRule = coverageRuleGenerator.generate(secondGenerationContext, nonPayEdiCode);

        assertThat(payRule.getCoverageRate()).isEqualTo(90);
        assertThat(payRule.getDeductibleAmount()).isEqualTo(10000);
        assertThat(nonPayRule.getCoverageRate()).isEqualTo(80);
        assertThat(nonPayRule.getDeductibleAmount()).isEqualTo(20000);
        assertThat(nonPayRule.getDisclaimer()).contains("2세대", "표준화", "자기부담 10~20%");
    }

    @Test
    void 삼세대는_비급여를_특약_분리_항목으로_처리한다() {
        EdiCode payEdiCode = createEdiCode("EDI008", "외래 진료", "일반", PayType.PAY, FeeType.MEDICAL);
        EdiCode nonPayEdiCode = createEdiCode("EDI009", "비급여 MRI 검사", "MRI", PayType.NON_PAY, FeeType.MEDICAL);
        CoverageRuleContext thirdGenerationContext =
                new CoverageRuleContext(1L, InsuranceGeneration.THIRD);

        CoverageRule payRule = coverageRuleGenerator.generate(thirdGenerationContext, payEdiCode);
        CoverageRule nonPayRule = coverageRuleGenerator.generate(thirdGenerationContext, nonPayEdiCode);

        assertThat(payRule.getCoverageRate()).isEqualTo(80);
        assertThat(payRule.getDeductibleAmount()).isEqualTo(10000);
        assertThat(payRule.getDisclaimer()).contains("3세대", "급여 기본계약", "비급여 특약");
        assertThat(nonPayRule.getCoverageRate()).isEqualTo(70);
        assertThat(nonPayRule.getDeductibleAmount()).isEqualTo(30000);
        assertThat(nonPayRule.getDisclaimer()).contains("비급여", "특약 분리", "항목별 한도");
    }

    @Test
    void 사세대는_비급여_이용량_기반_보험료_차등_대상임을_표시한다() {
        EdiCode payEdiCode = createEdiCode("EDI010", "외래 진료", "일반", PayType.PAY, FeeType.MEDICAL);
        EdiCode nonPayEdiCode = createEdiCode("EDI011", "비급여 주사 치료", "주사", PayType.NON_PAY, FeeType.MEDICAL);
        CoverageRuleContext fourthGenerationContext =
                new CoverageRuleContext(1L, InsuranceGeneration.FOURTH);

        CoverageRule payRule = coverageRuleGenerator.generate(fourthGenerationContext, payEdiCode);
        CoverageRule nonPayRule = coverageRuleGenerator.generate(fourthGenerationContext, nonPayEdiCode);

        assertThat(payRule.getCoverageRate()).isEqualTo(80);
        assertThat(payRule.getDeductibleAmount()).isEqualTo(10000);
        assertThat(payRule.getDisclaimer()).contains("4세대", "급여 20%", "비급여 30%", "보험료 차등");
        assertThat(nonPayRule.getCoverageRate()).isEqualTo(70);
        assertThat(nonPayRule.getDeductibleAmount()).isEqualTo(30000);
        assertThat(nonPayRule.getDisclaimer()).contains("비급여", "이용량 기반", "연간 비급여 사용량");
    }

    private EdiCode createEdiCode(
            String code,
            String treatmentName,
            String feeDivisionNumber,
            PayType payType,
            FeeType feeType
    ) {
        return EdiCode.create(
                code,
                treatmentName,
                feeDivisionNumber,
                payType,
                100000,
                BigDecimal.valueOf(123.45),
                LocalDate.of(2026, 1, 1),
                feeType
        );
    }
}
