package com.silsonfit.silsonfit_api.domain.calculation.controller;

import com.silsonfit.silsonfit_api.domain.calculation.client.EdiCodeClient;
import com.silsonfit.silsonfit_api.domain.calculation.entity.EdiCode;
import com.silsonfit.silsonfit_api.domain.calculation.enums.FeeType;
import com.silsonfit.silsonfit_api.domain.calculation.enums.PayType;
import com.silsonfit.silsonfit_api.global.common.ApiResponse;
import com.silsonfit.silsonfit_api.global.error.BusinessException;
import com.silsonfit.silsonfit_api.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 개발용 EDI 코드 조회 API
 *
 * - real-edi profile에서만 활성화되고, prod profile에서는 비활성화된다.
 * - 실제 공공 EDI API 연동 상태를 확인하기 위한 임시 API다.
 */
@Profile("real-edi & !prod")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dev/edi-codes")
public class DevEdiCodeController {

    private final EdiCodeClient ediCodeClient;

    /**
     * 외부 EDI Client 직접 조회
     */
    @GetMapping("/{code}")
    public ApiResponse<DevEdiCodeResponse> getEdiCode(
            @PathVariable String code
    ) {
        EdiCode ediCode = ediCodeClient.fetchByCode(code)
                .orElseThrow(() -> new BusinessException(ErrorCode.EDI_CODE_NOT_FOUND));

        return ApiResponse.success(DevEdiCodeResponse.from(ediCode));
    }

    public record DevEdiCodeResponse(
            String code,
            String treatmentName,
            String feeDivisionNumber,
            PayType payType,
            Integer unitPrice,
            BigDecimal relativeValuePoint,
            LocalDate effectiveStartDate,
            FeeType feeType
    ) {

        public static DevEdiCodeResponse from(EdiCode ediCode) {
            return new DevEdiCodeResponse(
                    ediCode.getCode(),
                    ediCode.getTreatmentName(),
                    ediCode.getFeeDivisionNumber(),
                    ediCode.getPayType(),
                    ediCode.getUnitPrice(),
                    ediCode.getRelativeValuePoint(),
                    ediCode.getEffectiveStartDate(),
                    ediCode.getFeeType()
            );
        }
    }
}
