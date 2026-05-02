package com.silsonfit.silsonfit_api.domain.insurance.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 보장 구조
 */
@Getter
@AllArgsConstructor
public enum CoverageStructure {

    COVERED_FOCUSED("급여중심"),
    COVERED_AND_UNCOVERED("급여+비급여"),
    THREE_UNCOVERED("3대비급여"),
    UNCOVERED_INCLUDED("비급여포함"),
    RIDER_INCLUDED("특약포함"),
    INPATIENT_OUTPATIENT("입·통원포함"),
    OUTPATIENT_FOCUSED("통원중심"),
    BASIC_FOCUSED("기본형중심"),
    PRESCRIPTION_INCLUDED("처방조제포함");

    private final String displayName;
}
