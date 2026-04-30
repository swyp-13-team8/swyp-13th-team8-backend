package com.silsonfit.silsonfit_api.domain.calculation.service;

import com.silsonfit.silsonfit_api.domain.calculation.client.EdiCodeClient;
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
 * - DB에 없는 경우 외부 EDI 수가 코드 Client로 조회 후 저장한다.
 */
@Service
@RequiredArgsConstructor
public class EdiCodeResolver {

    private final EdiCodeRepository ediCodeRepository;
    private final EdiCodeClient ediCodeClient;

    /**
     * 수가 코드에 해당하는 EDI 코드 조회
     *
     * @param code 수가 코드
     * @return EDI 코드
     */
    @Transactional
    public EdiCode resolve(String code) {
        if (!StringUtils.hasText(code)) {
            throw new BusinessException(ErrorCode.EDI_CODE_NOT_FOUND);
        }

        String normalizedCode = code.trim();

        return ediCodeRepository.findByCode(normalizedCode)
                .orElseGet(() -> fetchAndSave(normalizedCode));
    }

    /**
     * 외부 EDI Client 조회 후 저장
     */
    private EdiCode fetchAndSave(String code) {
        EdiCode fetchedEdiCode = ediCodeClient.fetchByCode(code)
                .orElseThrow(() -> new BusinessException(ErrorCode.EDI_CODE_NOT_FOUND));

        return ediCodeRepository.save(fetchedEdiCode);
    }
}
