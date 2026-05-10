package com.silsonfit.silsonfit_api.domain.calculation.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 급여 / 비급여 여부
 */
@Getter
@RequiredArgsConstructor
public enum PayType {

    PAY("급여"),
    NON_PAY("비급여"),
    UNKNOWN("급여 여부 모름");

    private final String description;
}
