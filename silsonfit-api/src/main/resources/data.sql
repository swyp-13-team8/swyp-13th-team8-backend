-- 개발용 EDI 미입력 fallback CoverageRule seed
-- 운영 환경에서는 Flyway 또는 관리자 API로 관리한다.

insert into coverage_rule (
    insurance_id,
    generation,
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
-- 1세대: 자기부담이 거의 없는 한도 중심 러프 룰
(
    null, 'FIRST', null, 'OUTPATIENT', 'MRI', 'TREATMENT',
    true, 100, 0, null,
    '["개발용 1세대 외래 MRI fallback 보장 룰","1세대는 자기부담이 거의 없는 구조로 러프 계산"]',
    '개발용 임시 보장 룰입니다.'
),
(
    null, 'FIRST', null, 'OUTPATIENT', 'CT', 'TREATMENT',
    true, 100, 0, null,
    '["개발용 1세대 외래 CT fallback 보장 룰","1세대는 자기부담이 거의 없는 구조로 러프 계산"]',
    '개발용 임시 보장 룰입니다.'
),
(
    null, 'FIRST', null, 'OUTPATIENT', 'GENERAL_TREATMENT', 'TREATMENT',
    true, 100, 0, null,
    '["개발용 1세대 외래 일반진료 fallback 보장 룰","1세대는 급여/비급여 대부분을 넓게 보장하는 구조로 러프 계산"]',
    '개발용 임시 보장 룰입니다.'
),
(
    null, 'FIRST', null, 'MEDICATION', 'GENERAL_TREATMENT', 'TREATMENT',
    true, 100, 0, null,
    '["개발용 1세대 약제 fallback 보장 룰","1세대는 자기부담이 거의 없는 구조로 러프 계산"]',
    '개발용 임시 보장 룰입니다.'
),

-- 2세대: 자기부담 도입을 반영한 러프 룰
(
    null, 'SECOND', null, 'OUTPATIENT', 'MRI', 'TREATMENT',
    true, 80, 20000, null,
    '["개발용 2세대 외래 MRI fallback 보장 룰","2세대는 자기부담 도입 구조를 반영한 러프 계산"]',
    '개발용 임시 보장 룰입니다.'
),
(
    null, 'SECOND', null, 'OUTPATIENT', 'CT', 'TREATMENT',
    true, 80, 20000, null,
    '["개발용 2세대 외래 CT fallback 보장 룰","2세대는 자기부담 도입 구조를 반영한 러프 계산"]',
    '개발용 임시 보장 룰입니다.'
),
(
    null, 'SECOND', null, 'OUTPATIENT', 'GENERAL_TREATMENT', 'TREATMENT',
    true, 90, 10000, null,
    '["개발용 2세대 외래 일반진료 fallback 보장 룰","2세대 급여성 진료는 낮은 자기부담 기준으로 러프 계산"]',
    '개발용 임시 보장 룰입니다.'
),
(
    null, 'SECOND', null, 'MEDICATION', 'GENERAL_TREATMENT', 'TREATMENT',
    true, 90, 8000, null,
    '["개발용 2세대 약제 fallback 보장 룰","2세대 약제비는 낮은 자기부담 기준으로 러프 계산"]',
    '개발용 임시 보장 룰입니다.'
),

-- 3세대: 기본계약 급여 영역 + 비급여 특약 분리 러프 룰
(
    null, 'THIRD', null, 'OUTPATIENT', 'GENERAL_TREATMENT', 'TREATMENT',
    true, 80, 10000, null,
    '["개발용 3세대 외래 일반진료 fallback 보장 룰","3세대 기본계약 급여 영역 기준 러프 계산"]',
    '개발용 임시 보장 룰입니다.'
),
(
    null, 'THIRD', null, 'MEDICATION', 'GENERAL_TREATMENT', 'TREATMENT',
    true, 80, 10000, null,
    '["개발용 3세대 약제 fallback 보장 룰","3세대 기본계약 급여 영역 기준 러프 계산"]',
    '개발용 임시 보장 룰입니다.'
),
(
    null, 'THIRD', null, 'OUTPATIENT', 'MRI', 'TREATMENT',
    true, 70, 30000, null,
    '["개발용 3세대 외래 MRI 비급여 특약 fallback 보장 룰","3세대는 비급여를 특약으로 분리하는 구조를 반영"]',
    '개발용 임시 보장 룰입니다.'
),
(
    null, 'THIRD', null, 'OUTPATIENT', 'CT', 'TREATMENT',
    true, 70, 30000, null,
    '["개발용 3세대 외래 CT fallback 보장 룰","3세대 고액 검사 항목은 높은 자기부담 기준으로 러프 계산"]',
    '개발용 임시 보장 룰입니다.'
),
(
    null, 'THIRD', null, 'OUTPATIENT', 'MANUAL_THERAPY', 'TREATMENT',
    true, 70, 30000, null,
    '["개발용 3세대 외래 도수치료 비급여 특약 fallback 보장 룰","3세대 비급여 특약 분리 항목 기준 러프 계산"]',
    '개발용 임시 보장 룰입니다.'
),
(
    null, 'THIRD', null, 'OUTPATIENT', 'SHOCKWAVE_THERAPY', 'TREATMENT',
    true, 70, 30000, null,
    '["개발용 3세대 외래 체외충격파 비급여 특약 fallback 보장 룰","3세대 비급여 특약 분리 항목 기준 러프 계산"]',
    '개발용 임시 보장 룰입니다.'
),
(
    null, 'THIRD', null, 'OUTPATIENT', 'INJECTION', 'TREATMENT',
    true, 70, 30000, null,
    '["개발용 3세대 외래 주사치료 비급여 특약 fallback 보장 룰","3세대 비급여 특약 분리 항목 기준 러프 계산"]',
    '개발용 임시 보장 룰입니다.'
),

-- 4세대: 급여/비급여 분리와 높은 비급여 자기부담을 반영한 러프 룰
(
    null, 'FOURTH', null, 'OUTPATIENT', 'GENERAL_TREATMENT', 'TREATMENT',
    true, 80, 10000, null,
    '["개발용 4세대 외래 일반진료 fallback 보장 룰","4세대 급여 영역 기준 러프 계산"]',
    '개발용 임시 보장 룰입니다.'
),
(
    null, 'FOURTH', null, 'MEDICATION', 'GENERAL_TREATMENT', 'TREATMENT',
    true, 80, 10000, null,
    '["개발용 4세대 약제 fallback 보장 룰","4세대 급여 영역 기준 러프 계산"]',
    '개발용 임시 보장 룰입니다.'
),
(
    null, 'FOURTH', null, 'OUTPATIENT', 'MRI', 'TREATMENT',
    true, 70, 30000, null,
    '["개발용 4세대 외래 MRI 비급여 fallback 보장 룰","4세대 비급여 자기부담 기준 러프 계산"]',
    '개발용 임시 보장 룰입니다.'
),
(
    null, 'FOURTH', null, 'OUTPATIENT', 'CT', 'TREATMENT',
    true, 70, 30000, null,
    '["개발용 4세대 외래 CT fallback 보장 룰","4세대 고액 검사 항목 기준 러프 계산"]',
    '개발용 임시 보장 룰입니다.'
),
(
    null, 'FOURTH', null, 'OUTPATIENT', 'MANUAL_THERAPY', 'TREATMENT',
    true, 70, 30000, null,
    '["개발용 4세대 외래 도수치료 비급여 fallback 보장 룰","4세대 비급여 자기부담 기준 러프 계산"]',
    '개발용 임시 보장 룰입니다.'
),
(
    null, 'FOURTH', null, 'OUTPATIENT', 'SHOCKWAVE_THERAPY', 'TREATMENT',
    true, 70, 30000, null,
    '["개발용 4세대 외래 체외충격파 비급여 fallback 보장 룰","4세대 비급여 자기부담 기준 러프 계산"]',
    '개발용 임시 보장 룰입니다.'
),
(
    null, 'FOURTH', null, 'OUTPATIENT', 'INJECTION', 'TREATMENT',
    true, 70, 30000, null,
    '["개발용 4세대 외래 주사치료 비급여 fallback 보장 룰","4세대 비급여 자기부담 기준 러프 계산"]',
    '개발용 임시 보장 룰입니다.'
),
(
    null, 'FOURTH', null, 'OUTPATIENT', 'MRI', 'CHECKUP',
    false, 0, 0, null,
    '["개발용 MRI 검진 목적 비보장 fallback 룰","건강검진 목적은 보장 제외 임시 기준"]',
    '개발용 임시 보장 룰입니다.'
);
