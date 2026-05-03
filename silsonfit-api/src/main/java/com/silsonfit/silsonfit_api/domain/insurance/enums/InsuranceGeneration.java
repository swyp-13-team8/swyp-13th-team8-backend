package com.silsonfit.silsonfit_api.domain.insurance.enums;

import com.silsonfit.silsonfit_api.global.error.BusinessException;
import com.silsonfit.silsonfit_api.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.YearMonth;

/**
 * 보험 세대 판별 enum
 *
 * 가입 연월 기준으로 1~4세대를 판별한다.
 * 별도 테이블 없이 도메인 내부에서 처리.
 */
@Getter
@AllArgsConstructor
public enum InsuranceGeneration {

    FIRST(1, null, YearMonth.of(2009, 7),
            "1세대: 2009년 7월 이전 가입"),
    SECOND(2, YearMonth.of(2009, 8), YearMonth.of(2017, 3),
            "2세대: 2009년 8월 ~ 2017년 3월 가입"),
    THIRD(3, YearMonth.of(2017, 4), YearMonth.of(2021, 6),
            "3세대: 2017년 4월 ~ 2021년 6월 가입"),
    FOURTH(4, YearMonth.of(2021, 7), null,
            "4세대: 2021년 7월 이후 가입");

    private final int generation;
    private final YearMonth startMonth;
    private final YearMonth endMonth;
    private final String description;

    /**
     * 가입 연월로 세대 판별
     *
     * @param subscribedYearMonth 가입 연월 (예: "2020-03")
     * @return 해당 세대 enum
     */
    public static InsuranceGeneration from(String subscribedYearMonth) {
        YearMonth target;
        try {
            target = YearMonth.parse(subscribedYearMonth);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_SUBSCRIBED_DATE);
        }

        for (InsuranceGeneration gen : values()) {
            boolean afterStart = (gen.startMonth == null) || !target.isBefore(gen.startMonth);
            boolean beforeEnd = (gen.endMonth == null) || !target.isAfter(gen.endMonth);

            if (afterStart && beforeEnd) {
                return gen;
            }
        }

        throw new BusinessException(ErrorCode.INVALID_SUBSCRIBED_DATE);
    }
}
