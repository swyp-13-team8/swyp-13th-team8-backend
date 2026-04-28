package com.silsonfit.silsonfit_api.domain.calculation.entity;

import com.silsonfit.silsonfit_api.domain.calculation.enums.PurposeType;
import com.silsonfit.silsonfit_api.domain.calculation.enums.TreatmentCategory;
import com.silsonfit.silsonfit_api.domain.calculation.enums.VisitType;
import jakarta.persistence.*;
import lombok.*;

/**
 * 보험별 보장 계산 룰 엔티티
 *
 */
@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoverageRule {

    /** 보장 룰 PK */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coverage_rule_id")
    private Long id;

    /** 보험 ID */
    @Column(name = "insurance_id", nullable = false)
    private Long insuranceId;

    /** EDI 코드 */
    @Column(name = "edi_code")
    private String ediCode;

    /** 진료 유형 */
    @Enumerated(EnumType.STRING)
    @Column(name = "visit_type", nullable = false)
    private VisitType visitType;

    /** 진료 항목 */
    @Enumerated(EnumType.STRING)
    @Column(name = "treatment_category", nullable = false)
    private TreatmentCategory treatmentCategory;

    /** 진료 목적 */
    @Enumerated(EnumType.STRING)
    @Column(name = "purpose_type", nullable = false)
    private PurposeType purposeType;

    /** 보장 여부 */
    @Column(name = "is_covered", nullable = false)
    private Boolean isCovered;

    /** 보장률 (%) */
    @Column(name = "coverage_rate", nullable = false)
    private Integer coverageRate;

    /** 고정 자기부담금 */
    @Column(name = "deductible_amount", nullable = false)
    private Integer deductibleAmount;

    /** 최대 보장 한도 */
    @Column(name = "limit_amount")
    private Integer limitAmount;

    /** 계산 근거 설명 */
    @Column(name = "basis", nullable = false)
    private String basis;

    /** 면책 또는 주의사항 */
    @Column(name = "disclaimer")
    private String disclaimer;

    /**
     * 보장 룰 생성
     */
    public static CoverageRule create(
            Long insuranceId,
            String ediCode,
            VisitType visitType,
            TreatmentCategory treatmentCategory,
            PurposeType purposeType,
            Boolean isCovered,
            Integer coverageRate,
            Integer deductibleAmount,
            Integer limitAmount,
            String basis,
            String disclaimer
    ) {
        return CoverageRule.builder()
                .insuranceId(insuranceId)
                .ediCode(ediCode)
                .visitType(visitType)
                .treatmentCategory(treatmentCategory)
                .purposeType(purposeType)
                .isCovered(isCovered)
                .coverageRate(coverageRate)
                .deductibleAmount(deductibleAmount)
                .limitAmount(limitAmount)
                .basis(basis)
                .disclaimer(disclaimer)
                .build();
    }
}