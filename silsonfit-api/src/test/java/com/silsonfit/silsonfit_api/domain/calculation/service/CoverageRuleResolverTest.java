package com.silsonfit.silsonfit_api.domain.calculation.service;

import com.silsonfit.silsonfit_api.domain.calculation.entity.CoverageRule;
import com.silsonfit.silsonfit_api.domain.calculation.enums.PurposeType;
import com.silsonfit.silsonfit_api.domain.calculation.enums.TreatmentCategory;
import com.silsonfit.silsonfit_api.domain.calculation.enums.VisitType;
import com.silsonfit.silsonfit_api.domain.calculation.repository.CoverageRuleRepository;
import com.silsonfit.silsonfit_api.global.error.BusinessException;
import com.silsonfit.silsonfit_api.global.error.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class CoverageRuleResolverTest {

    @Autowired
    CoverageRuleResolver coverageRuleResolver;

    @Autowired
    CoverageRuleRepository coverageRuleRepository;

    @Test
    void EDI코드_기반_보장룰이_있으면_우선_반환한다() {
        CoverageRule ediRule = createCoverageRule(
                1L,
                "EDI001",
                VisitType.OUTPATIENT,
                TreatmentCategory.MRI,
                PurposeType.TREATMENT,
                "EDI 룰"
        );
        CoverageRule treatmentInfoRule = createCoverageRule(
                1L,
                null,
                VisitType.OUTPATIENT,
                TreatmentCategory.MRI,
                PurposeType.TREATMENT,
                "진료정보 룰"
        );
        coverageRuleRepository.save(ediRule);
        coverageRuleRepository.save(treatmentInfoRule);

        CoverageRule resolvedRule = coverageRuleResolver.resolve(
                1L,
                "EDI001",
                VisitType.OUTPATIENT,
                TreatmentCategory.MRI,
                PurposeType.TREATMENT
        );

        assertThat(resolvedRule.getBasis()).isEqualTo("EDI 룰");
    }

    @Test
    void EDI코드_기반_보장룰이_없으면_진료정보_기반_보장룰을_반환한다() {
        CoverageRule treatmentInfoRule = createCoverageRule(
                1L,
                null,
                VisitType.OUTPATIENT,
                TreatmentCategory.MRI,
                PurposeType.TREATMENT,
                "진료정보 룰"
        );
        coverageRuleRepository.save(treatmentInfoRule);

        CoverageRule resolvedRule = coverageRuleResolver.resolve(
                1L,
                "UNKNOWN_EDI",
                VisitType.OUTPATIENT,
                TreatmentCategory.MRI,
                PurposeType.TREATMENT
        );

        assertThat(resolvedRule.getBasis()).isEqualTo("진료정보 룰");
    }

    @Test
    void 조회되는_보장룰이_없으면_예외가_발생한다() {
        assertThatThrownBy(() -> coverageRuleResolver.resolve(
                1L,
                "UNKNOWN_EDI",
                VisitType.OUTPATIENT,
                TreatmentCategory.MRI,
                PurposeType.TREATMENT
        ))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.COVERAGE_RULE_NOT_FOUND.getMessage());
    }

    private CoverageRule createCoverageRule(
            Long insuranceId,
            String ediCode,
            VisitType visitType,
            TreatmentCategory treatmentCategory,
            PurposeType purposeType,
            String basis
    ) {
        return CoverageRule.create(
                insuranceId,
                ediCode,
                visitType,
                treatmentCategory,
                purposeType,
                true,
                70,
                10000,
                null,
                basis,
                null
        );
    }
}
