package com.silsonfit.silsonfit_api.domain.insurance.enums;

import com.silsonfit.silsonfit_api.global.error.BusinessException;
import com.silsonfit.silsonfit_api.global.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 세대 판별 enum 단위 테스트
 */
class InsuranceGenerationTest {

    @Test
    @DisplayName("2009년 7월 이전 가입 → 1세대")
    void firstGeneration() {
        assertThat(InsuranceGeneration.from("2005-03").getGeneration()).isEqualTo(1);
        assertThat(InsuranceGeneration.from("2009-07").getGeneration()).isEqualTo(1);
    }

    @Test
    @DisplayName("2009년 8월 ~ 2017년 3월 가입 → 2세대")
    void secondGeneration() {
        assertThat(InsuranceGeneration.from("2009-08").getGeneration()).isEqualTo(2);
        assertThat(InsuranceGeneration.from("2015-06").getGeneration()).isEqualTo(2);
        assertThat(InsuranceGeneration.from("2017-03").getGeneration()).isEqualTo(2);
    }

    @Test
    @DisplayName("2017년 4월 ~ 2021년 6월 가입 → 3세대")
    void thirdGeneration() {
        assertThat(InsuranceGeneration.from("2017-04").getGeneration()).isEqualTo(3);
        assertThat(InsuranceGeneration.from("2020-03").getGeneration()).isEqualTo(3);
        assertThat(InsuranceGeneration.from("2021-06").getGeneration()).isEqualTo(3);
    }

    @Test
    @DisplayName("2021년 7월 이후 가입 → 4세대")
    void fourthGeneration() {
        assertThat(InsuranceGeneration.from("2021-07").getGeneration()).isEqualTo(4);
        assertThat(InsuranceGeneration.from("2024-01").getGeneration()).isEqualTo(4);
    }

    @Test
    @DisplayName("잘못된 형식의 가입 연월이면 INVALID_SUBSCRIBED_DATE 예외 발생")
    void invalidFormat() {
        assertThatThrownBy(() -> InsuranceGeneration.from("2020"))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_SUBSCRIBED_DATE);
    }
}
