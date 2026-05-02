package com.silsonfit.silsonfit_api.domain.insurance.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 계약 유형
 */
@Getter
@AllArgsConstructor
public enum ContractType {

    INDIVIDUAL("개인실손"),
    GROUP("단체실손"),
    CONVERSION("전환용"),
    RENEWAL("재개용"),
    SENIOR("노후실손"),
    PRE_EXISTING("유병력자");

    private final String displayName;
}
