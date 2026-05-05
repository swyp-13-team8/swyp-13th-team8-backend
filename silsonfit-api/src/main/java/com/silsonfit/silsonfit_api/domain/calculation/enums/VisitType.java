package com.silsonfit.silsonfit_api.domain.calculation.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 진료 유형
 */
@Getter
@RequiredArgsConstructor
public enum VisitType {

    OUTPATIENT("외래"),
    INPATIENT("입원"),
    MEDICATION("약제");

    private final String description;
}