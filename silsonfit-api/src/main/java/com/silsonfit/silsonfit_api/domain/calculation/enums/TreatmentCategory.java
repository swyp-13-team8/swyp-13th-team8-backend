package com.silsonfit.silsonfit_api.domain.calculation.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 진료 항목
 */
@Getter
@RequiredArgsConstructor
public enum TreatmentCategory {

    GENERAL("일반진료"),
    MRI("MRI"),
    CT("CT"),
    CHIROPRACTIC("도수치료"),
    SHOCKWAVE_THERAPY("체외충격파"),
    INJECTION("주사"),
    PHYSICAL_THERAPY("물리치료");

    private final String description;
}
