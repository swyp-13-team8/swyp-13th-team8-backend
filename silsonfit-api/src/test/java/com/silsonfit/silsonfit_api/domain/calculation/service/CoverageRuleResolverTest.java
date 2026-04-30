package com.silsonfit.silsonfit_api.domain.calculation.service;

import com.silsonfit.silsonfit_api.domain.calculation.entity.CoverageRule;
import com.silsonfit.silsonfit_api.domain.calculation.enums.InsuranceGeneration;
import com.silsonfit.silsonfit_api.domain.calculation.enums.PurposeType;
import com.silsonfit.silsonfit_api.domain.calculation.enums.TreatmentCategory;
import com.silsonfit.silsonfit_api.domain.calculation.enums.VisitType;
import com.silsonfit.silsonfit_api.domain.calculation.repository.CoverageRuleRepository;
import com.silsonfit.silsonfit_api.domain.calculation.repository.EdiCodeRepository;
import com.silsonfit.silsonfit_api.domain.calculation.vo.CoverageRuleContext;
import com.silsonfit.silsonfit_api.global.error.BusinessException;
import com.silsonfit.silsonfit_api.global.error.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class CoverageRuleResolverTest {

    CoverageRuleContext context = new CoverageRuleContext(1L, InsuranceGeneration.FOURTH);

    @Autowired
    CoverageRuleResolver coverageRuleResolver;

    @Autowired
    CoverageRuleRepository coverageRuleRepository;

    @Autowired
    EdiCodeRepository ediCodeRepository;

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
                context,
                "EDI001",
                VisitType.OUTPATIENT,
                TreatmentCategory.MRI,
                PurposeType.TREATMENT
        );

        assertThat(resolvedRule.getBasis()).containsExactly("EDI 룰");
    }

    @Test
    void EDI코드_기반_보장룰이_없으면_EDI정보로_보장룰을_생성한다() {
        CoverageRule treatmentInfoRule = createCoverageRule(
                1L,
                null,
                VisitType.OUTPATIENT,
                TreatmentCategory.MRI,
                PurposeType.TREATMENT,
                "진료정보 룰"
        );
        coverageRuleRepository.save(treatmentInfoRule);

        assertThat(ediCodeRepository.findByCode("MRI001")).isEmpty();
        assertThat(coverageRuleRepository.findByInsuranceIdAndEdiCode(1L, "MRI001")).isEmpty();

        CoverageRule resolvedRule = coverageRuleResolver.resolve(
                context,
                "MRI001",
                VisitType.OUTPATIENT,
                TreatmentCategory.MRI,
                PurposeType.TREATMENT
        );

        assertThat(resolvedRule.getEdiCode()).isEqualTo("MRI001");
        assertThat(resolvedRule.getTreatmentCategory()).isEqualTo(TreatmentCategory.MRI);
        assertThat(resolvedRule.getCoverageRate()).isEqualTo(70);
        assertThat(coverageRuleRepository.findByInsuranceIdAndEdiCode(1L, "MRI001")).isPresent();
        assertThat(ediCodeRepository.findByCode("MRI001")).isPresent();
    }

    @Test
    void EDI코드가_있지만_EDI정보도_없으면_예외가_발생한다() {
        assertThatThrownBy(() -> coverageRuleResolver.resolve(
                context,
                "UNKNOWN_EDI",
                VisitType.OUTPATIENT,
                TreatmentCategory.MRI,
                PurposeType.TREATMENT
        ))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.EDI_CODE_NOT_FOUND.getMessage());
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
                List.of(basis),
                null
        );
    }
}
