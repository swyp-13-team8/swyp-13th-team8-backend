package com.silsonfit.silsonfit_api.domain.calculation.service;

import com.silsonfit.silsonfit_api.domain.calculation.entity.CoverageRule;
import com.silsonfit.silsonfit_api.domain.calculation.entity.EdiCode;
import com.silsonfit.silsonfit_api.domain.calculation.enums.PurposeType;
import com.silsonfit.silsonfit_api.domain.calculation.enums.TreatmentCategory;
import com.silsonfit.silsonfit_api.domain.calculation.enums.VisitType;
import com.silsonfit.silsonfit_api.domain.calculation.repository.CoverageRuleRepository;
import com.silsonfit.silsonfit_api.domain.calculation.vo.CoverageRuleContext;
import com.silsonfit.silsonfit_api.global.error.BusinessException;
import com.silsonfit.silsonfit_api.global.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * 보장 룰 조회 Resolver
 *
 * - EDI 코드가 있으면 보험 ID + EDI 코드 기반 룰을 우선 조회한다.
 * - EDI 기반 룰이 없으면 EDI 코드 정보와 보험 정책으로 보장 룰을 생성한다.
 * - EDI 코드가 없으면 보험 세대 + 진료 유형/항목/목적 기반 룰로 대체 조회한다.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CoverageRuleResolver {

    private final CoverageRuleRepository coverageRuleRepository;
    private final EdiCodeResolver ediCodeResolver;
    private final CoverageRuleGenerator coverageRuleGenerator;

    /**
     * 계산 요청 조건에 맞는 보장 룰 조회
     *
     * @param context 보장 룰 생성 Context
     * @param ediCode EDI 코드
     * @param visitType 진료 유형
     * @param treatmentCategory 진료 항목
     * @param purposeType 진료 목적
     * @return 계산에 사용할 보장 룰
     */
    public CoverageRule resolve(
            CoverageRuleContext context,
            String ediCode,
            VisitType visitType,
            TreatmentCategory treatmentCategory,
            PurposeType purposeType
    ) {
        if (StringUtils.hasText(ediCode)) {
            String normalizedEdiCode = ediCode.trim();

            return coverageRuleRepository.findFirstByInsuranceIdAndEdiCodeOrderByIdDesc(context.insuranceId(), normalizedEdiCode)
                    .orElseGet(() -> generateAndSaveByEdiCode(context, normalizedEdiCode));
        }

        return resolveByTreatmentInfo(
                context,
                visitType,
                treatmentCategory,
                purposeType
        );
    }

    /**
     * EDI 코드 정보 기반 보장 룰 생성 후 저장
     */
    private CoverageRule generateAndSaveByEdiCode(
            CoverageRuleContext context,
            String ediCode
    ) {
        EdiCode resolvedEdiCode = ediCodeResolver.resolve(ediCode);
        CoverageRule generatedRule = coverageRuleGenerator.generate(context, resolvedEdiCode);

        return coverageRuleRepository.save(generatedRule);
    }

    /**
     * 진료 유형/항목/목적 기반 보장 룰 조회
     */
    private CoverageRule resolveByTreatmentInfo(
            CoverageRuleContext context,
            VisitType visitType,
            TreatmentCategory treatmentCategory,
            PurposeType purposeType
    ) {
        return findRule(context, visitType, treatmentCategory, purposeType)
                .or(() -> findRule(context, visitType, TreatmentCategory.GENERAL, purposeType))
                .or(() -> findRule(context, VisitType.OUTPATIENT, treatmentCategory, purposeType))
                .or(() -> findRule(context, VisitType.OUTPATIENT, TreatmentCategory.GENERAL, purposeType))
                .or(() -> findRule(context, VisitType.OUTPATIENT, TreatmentCategory.GENERAL, PurposeType.TREATMENT))
                .orElseThrow(() -> new BusinessException(ErrorCode.COVERAGE_RULE_NOT_FOUND));
    }

    private Optional<CoverageRule> findRule(
            CoverageRuleContext context,
            VisitType visitType,
            TreatmentCategory treatmentCategory,
            PurposeType purposeType
    ) {
        List<CoverageRule> rules = coverageRuleRepository.findByInsuranceIdIsNullAndEdiCodeIsNullAndGenerationAndVisitTypeAndTreatmentCategoryAndPurposeTypeOrderByIdAsc(
                context.generation(),
                visitType,
                treatmentCategory,
                purposeType
        );

        if (rules.size() > 1) {
            log.warn(
                    "중복 fallback 보장 룰이 존재합니다. 첫 번째 룰을 사용합니다. generation={}, visitType={}, treatmentCategory={}, purposeType={}, count={}",
                    context.generation(),
                    visitType,
                    treatmentCategory,
                    purposeType,
                    rules.size()
            );
        }

        return rules.stream().findFirst();
    }
}
