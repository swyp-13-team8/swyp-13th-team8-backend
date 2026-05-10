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

    public static InsuranceGeneration from(int generation) {
        return switch (generation) {
            case 1 -> FIRST;
            case 2 -> SECOND;
            case 3 -> THIRD;
            case 4 -> FOURTH;
            case 5 -> FIFTH;
            default -> FOURTH;
        };
    }
}
