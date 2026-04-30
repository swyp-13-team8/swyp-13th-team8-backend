package com.silsonfit.silsonfit_api.domain.calculation.client;

import com.silsonfit.silsonfit_api.domain.calculation.entity.EdiCode;
import com.silsonfit.silsonfit_api.domain.calculation.enums.FeeType;
import com.silsonfit.silsonfit_api.domain.calculation.enums.PayType;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

/**
 * 공공 EDI API Fake Client
 *
 * - 실제 공공 API 연동 전까지 DB 미존재 EDI 코드 조회 흐름을 검증하기 위한 임시 구현체다.
 */
@Component
@Profile("!prod & !real-edi")
public class FakeEdiCodeClient implements EdiCodeClient {

    @Override
    public Optional<EdiCode> fetchByCode(String code) {
        return switch (code) {
            case "Z3000030" -> Optional.of(create(
                    "Z3000030",
                    "복약지도료(방문당)-토요09-13",
                    "약",
                    PayType.PAY,
                    260,
                    BigDecimal.valueOf(3.3),
                    LocalDate.of(2016, 1, 1),
                    FeeType.PHARMACY
            ));
            case "MRI001" -> Optional.of(create(
                    "MRI001",
                    "MRI 검사",
                    "MRI",
                    PayType.NON_PAY,
                    300000,
                    BigDecimal.valueOf(120.5),
                    LocalDate.of(2024, 1, 1),
                    FeeType.MEDICAL
            ));
            case "MANUAL001" -> Optional.of(create(
                    "MANUAL001",
                    "도수치료",
                    "도수",
                    PayType.NON_PAY,
                    100000,
                    BigDecimal.valueOf(45.0),
                    LocalDate.of(2024, 1, 1),
                    FeeType.MEDICAL
            ));
            default -> Optional.empty();
        };
    }

    private static EdiCode create(
            String code,
            String treatmentName,
            String feeDivisionNumber,
            PayType payType,
            Integer unitPrice,
            BigDecimal relativeValuePoint,
            LocalDate effectiveStartDate,
            FeeType feeType
    ) {
        return EdiCode.create(
                code,
                treatmentName,
                feeDivisionNumber,
                payType,
                unitPrice,
                relativeValuePoint,
                effectiveStartDate,
                feeType
        );
    }
}
