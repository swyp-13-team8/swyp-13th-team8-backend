package com.silsonfit.silsonfit_api.domain.calculation.dto;

import com.silsonfit.silsonfit_api.domain.calculation.enums.HospitalType;
import com.silsonfit.silsonfit_api.domain.calculation.enums.PayType;
import com.silsonfit.silsonfit_api.domain.calculation.enums.PurposeType;
import com.silsonfit.silsonfit_api.domain.calculation.enums.TreatmentCategory;
import com.silsonfit.silsonfit_api.domain.calculation.enums.VisitType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 실손 보험 계산 요청 DTO
 */
@Getter
@NoArgsConstructor
public class CalculationRequest {

    /** 보험 ID */
    @NotBlank
    private String insuranceId;

    /** 의료비 */
    @NotNull
    @Positive
    private Integer medicalCost;

    /** 진료 유형 */
    @NotNull
    private VisitType visitType;

    /** 진료 항목 */
    @NotNull
    private TreatmentCategory treatmentCategory;

    /** 진료 목적 */
    @NotNull
    private PurposeType purposeType;

    /** EDI 코드 (선택) */
    private String ediCode;

    /** 병원 유형 */
    @NotNull
    private HospitalType hospitalType;

    /** 급여 여부 */
    @NotNull
    private PayType payType;
}
