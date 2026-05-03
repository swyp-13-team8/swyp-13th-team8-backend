package com.silsonfit.silsonfit_api.domain.calculation.client;

import com.silsonfit.silsonfit_api.domain.calculation.entity.EdiCode;

import java.util.Optional;

/**
 * 외부 EDI 수가 코드 조회 Client
 */
public interface EdiCodeClient {

    /**
     * 수가 코드 기반 EDI 코드 조회
     *
     * @param code 수가 코드
     * @return 조회된 EDI 코드
     */
    Optional<EdiCode> fetchByCode(String code);
}
