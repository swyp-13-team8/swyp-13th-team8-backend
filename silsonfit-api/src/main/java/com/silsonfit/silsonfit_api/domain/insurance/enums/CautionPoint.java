package com.silsonfit.silsonfit_api.domain.insurance.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 주의 포인트
 */
@Getter
@AllArgsConstructor
public enum CautionPoint {

    HIGH_DEDUCTIBLE("자기부담높음"),
    RENEWAL_TYPE("갱신형"),
    RE_ENROLLMENT("재가입형"),
    LIMITED_COVERAGE("보장제한많음"),
    CLAIM_CAUTION("청구유의"),
    DISCLAIMER_CHECK("면책확인"),
    THREE_UNCOVERED_CAUTION("3대비급여주의"),
    PREMIUM_ROOM_CAUTION("상급병실주의");

    private final String displayName;
}
