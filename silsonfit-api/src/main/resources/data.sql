-- 개발용 EDI 미입력 fallback CoverageRule seed
-- 운영 환경에서는 Flyway 또는 관리자 API로 관리한다.

insert into coverage_rule (
    insurance_id,
    edi_code,
    visit_type,
    treatment_category,
    purpose_type,
    is_covered,
    coverage_rate,
    deductible_amount,
    limit_amount,
    basis,
    disclaimer
) values
(
    1,
    null,
    'OUTPATIENT',
    'MRI',
    'TREATMENT',
    true,
    70,
    30000,
    null,
    '["개발용 4세대 외래 MRI 치료 목적 fallback 보장 룰","EDI 코드 미입력 요청을 위한 임시 기준"]',
    '개발용 임시 보장 룰입니다.'
),
(
    1,
    null,
    'OUTPATIENT',
    'MRI',
    'CHECKUP',
    false,
    0,
    0,
    null,
    '["개발용 MRI 검진 목적 비보장 fallback 룰","건강검진 목적은 보장 제외 임시 기준"]',
    '개발용 임시 보장 룰입니다.'
),
(
    1,
    null,
    'OUTPATIENT',
    'MANUAL_THERAPY',
    'TREATMENT',
    true,
    70,
    30000,
    null,
    '["개발용 4세대 외래 도수치료 fallback 보장 룰","EDI 코드 미입력 요청을 위한 임시 기준"]',
    '개발용 임시 보장 룰입니다.'
),
(
    1,
    null,
    'MEDICATION',
    'GENERAL_TREATMENT',
    'TREATMENT',
    true,
    80,
    10000,
    null,
    '["개발용 4세대 약제 fallback 보장 룰","EDI 코드 미입력 요청을 위한 임시 기준"]',
    '개발용 임시 보장 룰입니다.'
);
