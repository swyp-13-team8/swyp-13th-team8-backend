package com.silsonfit.silsonfit_api.domain.calculation.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 실손 보험 세대
 */
@Getter
@RequiredArgsConstructor
public enum InsuranceGeneration {

    FIRST("1세대"),
    SECOND("2세대"),
    THIRD("3세대"),
    FOURTH("4세대"),
    FIFTH("5세대");

    private final String description;
}