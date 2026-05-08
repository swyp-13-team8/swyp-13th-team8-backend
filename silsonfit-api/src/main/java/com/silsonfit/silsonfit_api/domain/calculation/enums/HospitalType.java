package com.silsonfit.silsonfit_api.domain.calculation.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 병원 유형
 */
@Getter
@RequiredArgsConstructor
public enum HospitalType {

    CLINIC("의원"),
    GENERAL_HOSPITAL("종합병원"),
    TERTIARY_HOSPITAL("상급종합병원"),
    UNKNOWN("병원 유형 모름");

    private final String description;
}
