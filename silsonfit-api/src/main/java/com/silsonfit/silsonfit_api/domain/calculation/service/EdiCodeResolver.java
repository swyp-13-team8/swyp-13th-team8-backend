package com.silsonfit.silsonfit_api.domain.calculation.service;

import com.silsonfit.silsonfit_api.domain.calculation.entity.EdiCode;
import com.silsonfit.silsonfit_api.domain.calculation.repository.EdiCodeRepository;
import com.silsonfit.silsonfit_api.global.error.BusinessException;
import com.silsonfit.silsonfit_api.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * EDI 코드 조회 Resolver
 *
 * - 우선 DB에 저장된 EDI 수가 코드를 조회한다.
 * - DB에 없는 경우 추후 공공 API 조회 로직으로 확장한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EdiCodeResolver {

    private final EdiCodeRepository ediCodeRepository;

    /**
     * 수가 코드에 해당하는 EDI 코드 조회
     *
     * @param code 수가 코드
     * @return EDI 코드
     */
    public EdiCode resolve(String code) {
        if (!StringUtils.hasText(code)) {
            throw new BusinessException(ErrorCode.EDI_CODE_NOT_FOUND);
        }

        return ediCodeRepository.findByCode(code.trim())
                .orElseThrow(() -> new BusinessException(ErrorCode.EDI_CODE_NOT_FOUND));
    }
}
