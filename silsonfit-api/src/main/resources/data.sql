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

-- 개발용 보험 상품 seed
-- 운영 환경에서는 Flyway 또는 관리자 API로 관리한다.
-- cautionPoint: 2~3세대 RENEWAL_TYPE (자동갱신), 4세대 RE_ENROLLMENT (5년 재가입) — 금감원 규정 기준

INSERT INTO insurances (company_name, product_name, contract_type, generation, coverage_structure, caution_point, pdf_file_url, pdf_file_name) VALUES
-- DB손해보험
('DB손해보험', '무배당 프로미라이프 실손의료비보험1701', 'INDIVIDUAL', 2, 'COVERED_AND_UNCOVERED', 'RENEWAL_TYPE', null, '무배당 프로미라이프 실손의료비보험1701_약관.pdf'),
('DB손해보험', '무배당 동부화재 다이렉트 실손의료비보험1701', 'INDIVIDUAL', 2, 'COVERED_AND_UNCOVERED', 'RENEWAL_TYPE', null, '무배당 동부화재 다이렉트 실손의료비보험1701_약관.pdf'),
('DB손해보험', '무배당 프로미라이프 실손의료비보험2101', 'INDIVIDUAL', 3, 'THREE_UNCOVERED', 'RENEWAL_TYPE', null, '무배당 프로미라이프 실손의료비보험2101_20210401_약관.pdf'),
('DB손해보험', '무배당 프로미라이프 다이렉트 실손의료비보험2101(CM)', 'INDIVIDUAL', 3, 'THREE_UNCOVERED', 'RENEWAL_TYPE', null, '무배당 프로미라이프 다이렉트 실손의료비보험2101(CM)_20210401_약관.pdf'),
('DB손해보험', '무배당 프로미라이프 간편실손의료비보험(유병력자용)2101', 'PRE_EXISTING', 3, 'THREE_UNCOVERED', 'RENEWAL_TYPE', null, '무배당 프로미라이프 간편실손의료비보험(유병력자용)2101_20210101_약관.pdf'),
('DB손해보험', '무배당 프로미라이프 실손의료비보험2604', 'INDIVIDUAL', 4, 'THREE_UNCOVERED', 'RE_ENROLLMENT', null, '무배당 프로미라이프 실손의료비보험2604_20260401_약관.pdf'),
('DB손해보험', '무배당 프로미라이프 다이렉트 실손의료비보험2604(CM)', 'INDIVIDUAL', 4, 'THREE_UNCOVERED', 'RE_ENROLLMENT', null, '무배당 프로미라이프 다이렉트 실손의료비보험2604(CM)_20260401_약관.pdf'),
('DB손해보험', '무배당 프로미라이프 간편실손의료비보험(유병력자용)2604', 'PRE_EXISTING', 4, 'THREE_UNCOVERED', 'RE_ENROLLMENT', null, '무배당 프로미라이프 간편실손의료비보험(유병력자용)2604_약관.pdf'),

-- KB손해보험
('KB손해보험', '무배당 KB손보실손의료비보장보험(16.09)', 'INDIVIDUAL', 2, 'COVERED_AND_UNCOVERED', 'RENEWAL_TYPE', null, '무배당 KB손보실손의료비보장보험(16.09)_약관.pdf'),
('KB손해보험', '무배당 KB손보다이렉트실손의료비보장보험(16.10)', 'INDIVIDUAL', 2, 'COVERED_AND_UNCOVERED', 'RENEWAL_TYPE', null, '무배당 KB손보다이렉트실손의료비보장보험(16.10)_약관.pdf'),
('KB손해보험', '무배당 KB손보 실손의료비보장보험(21.01)', 'INDIVIDUAL', 3, 'THREE_UNCOVERED', 'RENEWAL_TYPE', null, '무배당 KB손보 실손의료비보장보험(21.01)_약관.pdf'),
('KB손해보험', '무배당 KB손보 다이렉트실손의료비보장보험(21.01)', 'INDIVIDUAL', 3, 'THREE_UNCOVERED', 'RENEWAL_TYPE', null, '무배당 KB손보 다이렉트실손의료비보장보험(21.01)_약관.pdf'),
('KB손해보험', '무배당 KB손보 간편가입 실손의료비보장보험(21.01)', 'PRE_EXISTING', 3, 'THREE_UNCOVERED', 'RENEWAL_TYPE', null, '무배당 KB손보 간편가입 실손의료비보장보험(21.01)_약관.pdf'),
('KB손해보험', 'KB손보 실손의료비보장보험(무배당)(26.02)', 'INDIVIDUAL', 4, 'THREE_UNCOVERED', 'RE_ENROLLMENT', null, 'KB손보 실손의료비보장보험(무배당)(26.02)_약관.pdf'),
('KB손해보험', 'KB손보 다이렉트실손의료비보장보험(무배당)(26.02)', 'INDIVIDUAL', 4, 'THREE_UNCOVERED', 'RE_ENROLLMENT', null, 'KB손보 다이렉트실손의료비보장보험(무배당)(26.02)_약관.pdf'),
('KB손해보험', 'KB손보 간편가입 실손의료비보장보험(무배당)(26.01)', 'PRE_EXISTING', 4, 'THREE_UNCOVERED', 'RE_ENROLLMENT', null, 'KB손보 간편가입 실손의료비보장보험(무배당)(26.01)_약관.pdf'),

-- 삼성화재
('삼성화재', '무배당 삼성화재 실손의료비보험(1611.7)', 'INDIVIDUAL', 2, 'COVERED_AND_UNCOVERED', 'RENEWAL_TYPE', null, '무배당 삼성화재 실손의료비보험 (1611.7)_20170101_약관.pdf'),
('삼성화재', '무배당 삼성화재 다이렉트 실손의료비보험(1611.6)', 'INDIVIDUAL', 2, 'COVERED_AND_UNCOVERED', 'RENEWAL_TYPE', null, '무배당 삼성화재 다이렉트 실손의료비보험 (1611.6)_20170101_약관.pdf'),
('삼성화재', '무배당 삼성화재 실손의료비보험(1704.1)', 'INDIVIDUAL', 3, 'THREE_UNCOVERED', 'RENEWAL_TYPE', null, '무배당 삼성화재 실손의료비보험(1704.1)_20170401_약관.pdf'),
('삼성화재', '무배당 삼성화재 다이렉트 실손의료비보험(1704.1)', 'INDIVIDUAL', 3, 'THREE_UNCOVERED', 'RENEWAL_TYPE', null, '무배당 삼성화재 다이렉트 실손의료비보험(1704.1)_20170401_약관.pdf'),
('삼성화재', '무배당 삼성화재 다이렉트 유병력자 실손의료비보험(2101.3)', 'PRE_EXISTING', 3, 'THREE_UNCOVERED', 'RENEWAL_TYPE', null, '무배당 삼성화재 다이렉트 유병력자실손의료비보험(2101.3)_20210401_약관.pdf'),
('삼성화재', '무배당 삼성화재 실손의료비보험(2601.6)', 'INDIVIDUAL', 4, 'THREE_UNCOVERED', 'RE_ENROLLMENT', null, '무배당 삼성화재 실손의료비보험(2601.6)_20260101_약관.pdf'),
('삼성화재', '무배당 삼성화재 다이렉트 실손의료비보험(2601.6)', 'INDIVIDUAL', 4, 'THREE_UNCOVERED', 'RE_ENROLLMENT', null, '무배당 삼성화재 다이렉트 실손의료비보험(2601.6)_20260101_약관.pdf'),
('삼성화재', '무배당 삼성화재 유병력자 실손의료비보험(2601.13)', 'PRE_EXISTING', 4, 'THREE_UNCOVERED', 'RE_ENROLLMENT', null, '무배당 삼성화재 유병력자 실손의료비보험(2601.13)_20260101_약관.pdf'),

-- 현대해상
('현대해상', '(무)실손의료비보장보험(갱신형)(Hi1904)', 'INDIVIDUAL', 3, 'THREE_UNCOVERED', 'RENEWAL_TYPE', null, '(무)실손의료비보장보험(갱신형)(Hi1904)_약관.pdf'),
('현대해상', '현대해상 실손의료비보장보험(Hi2307)', 'INDIVIDUAL', 4, 'THREE_UNCOVERED', 'RE_ENROLLMENT', null, '현대해상 실손의료비보장보험(Hi2307)_약관.pdf'),
('현대해상', '현대해상 다이렉트 실손의료비보험(4세대D)', 'INDIVIDUAL', 4, 'THREE_UNCOVERED', 'RE_ENROLLMENT', null, '현대해상 다이렉트 실손의료비보험(4세대D)_20250701_약관.pdf'),
('현대해상', '현대해상 유병력자 실손의료비보장보험(Hi2504)', 'PRE_EXISTING', 4, 'THREE_UNCOVERED', 'RE_ENROLLMENT', null, '현대해상 유병력자 실손의료비보장보험(Hi2504)_약관.pdf'),

-- 메리츠화재
('메리츠화재', '무배당 메리츠 다이렉트 실손의료비보험(~2021.03)', 'INDIVIDUAL', 3, 'THREE_UNCOVERED', 'RENEWAL_TYPE', null, '무배당 메리츠 다이렉트 실손의료비보험(~2021.03)_20210101_약관.pdf'),
('메리츠화재', '무배당 메리츠 다이렉트 실손의료비보험(현재)', 'INDIVIDUAL', 4, 'THREE_UNCOVERED', 'RE_ENROLLMENT', null, '무배당 메리츠 다이렉트 실손의료비보험(현재)_20241001_약관.pdf'),

-- 표준약관 (세대별)
('표준약관', '1세대 표준약관', null, 1, 'COVERED_AND_UNCOVERED', null, null, null),
('표준약관', '2세대 표준약관', null, 2, 'COVERED_AND_UNCOVERED', 'RENEWAL_TYPE', null, null),
('표준약관', '3세대 표준약관', null, 3, 'THREE_UNCOVERED', 'RENEWAL_TYPE', null, '실손의료보험 표준약관(2018.11.06 개정)_3세대.pdf'),
('표준약관', '4세대 표준약관', null, 4, 'THREE_UNCOVERED', 'RE_ENROLLMENT', null, '실손의료보험 표준약관(2024.12.20 시행)_4세대최신.pdf');
