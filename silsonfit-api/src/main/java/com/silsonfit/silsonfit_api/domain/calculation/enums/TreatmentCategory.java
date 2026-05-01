package com.silsonfit.silsonfit_api.domain.calculation.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 진료 항목
 */
@Getter
@RequiredArgsConstructor
public enum TreatmentCategory {

    MRI("MRI"),
    CT("CT"),
    MANUAL_THERAPY("도수치료"),
    SHOCKWAVE_THERAPY("체외충격파"),
    INJECTION("주사"),
    PHYSICAL_THERAPY("물리치료"),
    GENERAL_TREATMENT("일반진료");

    private final String description;
}
