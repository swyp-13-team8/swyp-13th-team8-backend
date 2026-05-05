package com.silsonfit.silsonfit_api.domain.calculation.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 수가 유형
 */
@Getter
@RequiredArgsConstructor
public enum FeeType {

    MEDICAL("진료수가"),
    KOREAN_MEDICAL("한방수가"),
    PHARMACY("약국수가");

    private final String description;
}