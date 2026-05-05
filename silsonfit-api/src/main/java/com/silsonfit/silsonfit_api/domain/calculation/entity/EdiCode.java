package com.silsonfit.silsonfit_api.domain.calculation.entity;

import com.silsonfit.silsonfit_api.domain.calculation.enums.FeeType;
import com.silsonfit.silsonfit_api.domain.calculation.enums.PayType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * EDI 수가 코드 엔티티
 *
 * - 건강보험 청구 기준의 수가(행위) 정보를 저장하는 원본 데이터 테이블
 * - 공공데이터 API(수가 정보)를 기반으로 적재된다.
 * - 보험 보장 판단의 직접 기준이 아니라, CoverageRule 매칭을 위한 입력 데이터로 사용된다.
 */
@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "edi_code")
public class EdiCode {

    /** EDI 코드 PK */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "edi_code_id")
    private Long id;

    /**
     * 수가 코드
     * - 건강보험 행위 코드 (예: Z3000030)
     * - 동일 코드라도 적용 시점에 따라 값이 달라질 수 있음
     */
    @Column(name = "code", nullable = false, length = 20)
    private String code;

    /**
     * 수가 한글명
     * - 진료/처치/검사 등의 명칭
     */
    @Column(name = "treatment_name", nullable = false)
    private String treatmentName;

    /**
     * 수가 분류 번호
     * - 행위 분류 코드 (검사, 처치, 약 등)
     * - 보험 도메인 카테고리로 직접 사용하기에는 부족하며, 추가 매핑 필요
     */
    @Column(name = "fee_division_number")
    private String feeDivisionNumber;

    /**
     * 급여 여부
     * - 급여 / 비급여 구분
     * - 실손 보장 판단의 참고 값으로 사용됨
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "pay_type", nullable = false)
    private PayType payType;

    /**
     * 단가 (원)
     * - 해당 수가의 기본 금액
     */
    @Column(name = "unit_price")
    private Integer unitPrice;

    /**
     * 상대가치점수 (RVU)
     * - 수가 계산 시 사용되는 상대 가치
     */
    @Column(name = "relative_value_point")
    private BigDecimal relativeValuePoint;

    /**
     * 적용 시작일자
     * - 해당 수가가 유효한 시작 시점
     */
    @Column(name = "effective_start_date")
    private LocalDate effectiveStartDate;

    /**
     * 수가 유형
     * - 진료 / 한방 / 약국 구분
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "fee_type", nullable = false)
    private FeeType feeType;

    /**
     * 급여 여부 판단
     *
     * @return 급여 여부
     */
    public boolean isPay() {
        return this.payType == PayType.PAY;
    }

    /**
     * EDI 분류에 사용할 문자열 생성
     *
     * @return 수가 코드, 한글명, 수가 분류 번호를 합친 검색 문자열
     */
    public String getClassificationSource() {
        return String.join(
                " ",
                nullToEmpty(code),
                nullToEmpty(treatmentName),
                nullToEmpty(feeDivisionNumber)
        ).toUpperCase();
    }

    /**
     * EDI 분류 문자열의 키워드 포함 여부 판단
     *
     * @param keyword 검색 키워드
     * @return 포함 여부
     */
    public boolean containsKeyword(String keyword) {
        return getClassificationSource().contains(keyword.toUpperCase());
    }

    /**
     * EDI 코드 생성
     *
     * @param code 수가 코드
     * @param treatmentName 한글명
     * @param feeDivisionNumber 수가 분류 번호
     * @param payType 급여 여부
     * @param unitPrice 단가
     * @param relativeValuePoint 상대가치점수
     * @param effectiveStartDate 적용 시작일자
     * @param feeType 수가 유형
     */
    public static EdiCode create(
            String code,
            String treatmentName,
            String feeDivisionNumber,
            PayType payType,
            Integer unitPrice,
            BigDecimal relativeValuePoint,
            LocalDate effectiveStartDate,
            FeeType feeType
    ) {
        return EdiCode.builder()
                .code(code)
                .treatmentName(treatmentName)
                .feeDivisionNumber(feeDivisionNumber)
                .payType(payType)
                .unitPrice(unitPrice)
                .relativeValuePoint(relativeValuePoint)
                .effectiveStartDate(effectiveStartDate)
                .feeType(feeType)
                .build();
    }

    private String nullToEmpty(String value) {
        if (value == null) {
            return "";
        }

        return value;
    }
}
