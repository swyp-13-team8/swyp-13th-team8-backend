package com.silsonfit.silsonfit_api.domain.calculation.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 진료 목적
 */
@Getter
@RequiredArgsConstructor
public enum PurposeType {

    TREATMENT("치료 목적"),
    EXAMINATION("검사 목적"),
    UNKNOWN("잘 모름");

    private final String description;
}
