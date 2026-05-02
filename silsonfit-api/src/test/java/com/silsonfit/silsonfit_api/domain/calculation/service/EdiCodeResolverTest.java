package com.silsonfit.silsonfit_api.domain.calculation.service;

import com.silsonfit.silsonfit_api.domain.calculation.entity.EdiCode;
import com.silsonfit.silsonfit_api.domain.calculation.enums.FeeType;
import com.silsonfit.silsonfit_api.domain.calculation.enums.PayType;
import com.silsonfit.silsonfit_api.domain.calculation.repository.EdiCodeRepository;
import com.silsonfit.silsonfit_api.global.error.BusinessException;
import com.silsonfit.silsonfit_api.global.error.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class EdiCodeResolverTest {

    @Autowired
    EdiCodeResolver ediCodeResolver;

    @Autowired
    EdiCodeRepository ediCodeRepository;

    @Test
    void DB에_EDI코드가_있으면_반환한다() {
        ediCodeRepository.save(createEdiCode("EDI001"));

        EdiCode ediCode = ediCodeResolver.resolve("EDI001");

        assertThat(ediCode.getCode()).isEqualTo("EDI001");
        assertThat(ediCode.getTreatmentName()).isEqualTo("MRI 검사");
    }

    @Test
    void DB에_EDI코드가_없으면_예외가_발생한다() {
        assertThatThrownBy(() -> ediCodeResolver.resolve("UNKNOWN_EDI"))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.EDI_CODE_NOT_FOUND.getMessage());
    }

    @Test
    void EDI코드가_공백이면_예외가_발생한다() {
        assertThatThrownBy(() -> ediCodeResolver.resolve(" "))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.EDI_CODE_NOT_FOUND.getMessage());
    }

    private EdiCode createEdiCode(String code) {
        return EdiCode.create(
                code,
                "MRI 검사",
                "MRI",
                PayType.PAY,
                100000,
                BigDecimal.valueOf(123.45),
                LocalDate.of(2026, 1, 1),
                FeeType.MEDICAL
        );
    }
}
