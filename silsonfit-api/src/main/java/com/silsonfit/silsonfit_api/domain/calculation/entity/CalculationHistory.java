package com.silsonfit.silsonfit_api.domain.calculation.entity;

import com.silsonfit.silsonfit_api.domain.calculation.enums.TreatmentCategory;
import com.silsonfit.silsonfit_api.global.common.BaseCreatedTimeEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 실손 보험 계산 이력 엔티티
 */
@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CalculationHistory extends BaseCreatedTimeEntity {

    /** 계산 이력 PK */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "calculation_history_id")
    private Long id;

    // ===== 1. 요청 스냅샷 =====

    /** 사용자 식별자 */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 보험 ID */
    @Column(name = "insurance_id", nullable = false)
    private Long insuranceId;

    /** 의료비 입력값 */
    @Column(name = "medical_cost", nullable = false)
    private Integer medicalCost;

    /** 진료 항목 */
    @Enumerated(EnumType.STRING)
    @Column(name = "treatment_category", nullable = false)
    private TreatmentCategory treatmentCategory;

    /** EDI 코드 */
    @Column(name = "edi_code")
    private String ediCode;

    // ===== 2. 계산 결과 =====

    /** 보장 여부 */
    @Column(name = "is_covered", nullable = false)
    private Boolean isCovered;

    /** 예상 환급액 */
    @Column(name = "refund_amount", nullable = false)
    private Integer refundAmount;

    /** 총 자기부담금 */
    @Column(name = "deductible_amount", nullable = false)
    private Integer deductibleAmount;

    // ===== 3. 부가 정보 =====

    /** 즐겨찾기 여부 */
    @Column(name = "is_favorite", nullable = false)
    @Builder.Default
    private Boolean isFavorite = false;

    public static CalculationHistory create(
            Long userId,
            Long insuranceId,
            Integer medicalCost,
            TreatmentCategory treatmentCategory,
            String ediCode,
            Boolean isCovered,
            Integer refundAmount,
            Integer deductibleAmount
    ) {
        return CalculationHistory.builder()
                .userId(userId)
                .insuranceId(insuranceId)
                .medicalCost(medicalCost)
                .treatmentCategory(treatmentCategory)
                .ediCode(ediCode)
                .isCovered(isCovered)
                .refundAmount(refundAmount)
                .deductibleAmount(deductibleAmount)
                .build();
    }

    /** 즐겨찾기 토글 */
    public void toggleFavorite() {
        this.isFavorite = !this.isFavorite;
    }
}