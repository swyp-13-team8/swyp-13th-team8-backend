# Silsonfit

**보험 약관 이해가 어려운 사회초년생과 보험 초보자를 위한 실손보험 보장 여부, 예상 환급액 예측 서비스**

복잡한 보험 약관을 AI 기반으로 쉽게 분석하고, 실제 의료비 청구에 대한 보장 여부와 예상 환급액을 즉시 확인할 수 있습니다.
PDF 약관 파싱 및 Google Gemini AI를 활용해 주요 보장 항목을 요약하고,
사용자의 의료비 기록을 바탕으로 실손보험 약관에 따른 예상 환급액을 계산합니다.

---

## 목차

1. [프로젝트 소개](#-프로젝트-소개)
2. [프로젝트 개요](#-프로젝트-개요)
3. [팀원 / 역할](#-팀원--역할)
4. [기술 스택](#-기술-스택)
5. [주요 기능](#-주요-기능)
6. [DB / ERD](#-db--erd)
7. [API 명세서](#-api-명세서)

---

## 프로젝트 소개

**Silsonfit**은 보험 약관 이해를 돕고 정확한 예상 환급액을 계산해주는 서비스입니다.

### 주요 특징
- **보험 등록**: 가입한 실손보험 정보를 등록하고 관리
- **약관 분석**: 복잡한 보험 약관 PDF를 AI로 분석하여 주요 항목만 간단하게 요약
- **예상 환급액 계산**: 의료 영수증 기반으로 보장 여부 확인 및 환급금/자가부담금 계산
- **히스토리 관리**: 등록한 보험, 분석 기록, 계산 결과를 모두 저장하여 언제든 확인 가능

---

## 프로젝트 개요

### 디렉토리 구조

```
silsonfit-api/
├── src/
│   ├── main/
│   │   ├── java/com/silsonfit/silsonfit_api/
│   │   │   ├── domain/
│   │   │   │   ├── analysis/          # 약관 분석 (PDF 파싱, AI 기반 요약)
│   │   │   │   ├── auth/              # 인증/인가 (카카오 로그인, JWT)
│   │   │   │   ├── calculation/       # 보험금 계산 (실손 약관 기반 환급액 계산)
│   │   │   │   ├── insurance/         # 보험 등록/관리 (보험사, 상품, 세대)
│   │   │   │   └── user/              # 사용자 관리 (프로필 조회/수정)
│   │   │   └── global/
│   │   │       ├── auth/              # JWT 필터, 사용자 인증 객체
│   │   │       ├── common/            # 공통 응답 형식
│   │   │       └── error/             # 전역 예외 처리
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       ├── data.sql              # 보험 약관 기본 데이터
│   │       └── ...
│   └── test/
│       └── java/com/silsonfit/silsonfit_api/
├── build.gradle
├── Dockerfile
└── settings.gradle
```

### 아키텍처

```
┌─────────────────┐
│   클라이언트     │  (iOS/Android/Web)
└────────┬────────┘
         │
    ┌────▼──────────────────────┐
    │   Nginx (Reverse Proxy)   │ (포트: 80, 443)
    └────┬──────────────────────┘
         │
    ┌────▼───────────────────────────────────┐
    │  Spring Boot Application (포트: 8080)   │
    ├────────────────────────────────────────┤
    │  ┌──────────────┐                      │
    │  │  Controller  │                      │
    │  ├──────────────┤                      │
    │  │  Service     │                      │
    │  ├──────────────┤                      │
    │  │  Repository  │                      │
    │  └──────────────┘                      │
    └────┬───────────────────────────────────┘
         │
    ┌────┴──────────────────┐
    │                       │
┌───▼──────────┐   ┌───────▼─────────┐
│ PostgreSQL   │   │ AWS S3          │
│ (포트: 5432)  │   │ (파일 저장소)    │
└──────────────┘   └─────────────────┘

External Services:
│
├─ Google Gemini API (AI 분석)
├─ Kakao API (소셜 로그인)
└─ EDI 공공 API (의료비 코드)
```

---

## 팀원 / 역할

| 역할 | 이름 | 담당 기능 |
|------|------|---------|
| 백엔드 리드 | 박정수 | 예상 환급금 계산 |
| 백엔드 개발 | 정영준 | 인증/인가, 보험 등록, 마이페이지 |
| 백엔드 개발 | 김재현 | 약관 PDF 분석, AI API 연동 |

---

## 기술 스택

### Java & Spring Framework
- **Java 17**: 프로젝트 기본 언어
- **Spring Boot 3.5.13**: REST API 서버 프레임워크
- **Spring Security**: 사용자 인증/인가 처리
- **Spring Data JPA**: ORM 기반 데이터베이스 접근
- **Spring AI 1.1.4**: Google Gemini API 연동
- **Spring Boot Actuator**: 헬스 체크 및 메트릭 수집

### Database
- **PostgreSQL 15**: 프로덕션 데이터베이스
- **H2**: 로컬 개발/테스트용 인메모리 DB

### Authentication & Token
- **JJWT 0.12.6**: JWT 토큰 생성/파싱/검증
- **Kakao OAuth 2.0**: 소셜 로그인 연동

### File & PDF Processing
- **Apache PDFBox 3.0.2**: PDF 파싱 및 텍스트 추출
- **AWS SDK for Java (S3 2.25.11)**: 클라우드 파일 저장소

### External Services
- **Google Gemini API**: 약관 내용 AI 분석 및 요약
- **Kakao API**: 사용자 정보 조회
- **EDI 공공 API**: 의료 행위 코드 정보 조회

### Development & Documentation
- **Lombok**: 보일러플레이트 코드 제거
- **Springdoc OpenAPI 2.8.16**: Swagger/OpenAPI 자동 문서화
- **SLF4J & Logback**: 로깅

### Testing
- **JUnit 5**: 단위 테스트 프레임워크
- **Spring Boot Test**: 통합 테스트
- **Spring Security Test**: 보안 관련 테스트

### Build & Deployment
- **Gradle**: 빌드 및 의존성 관리
- **Docker**: 애플리케이션 컨테이너화
- **Docker Compose**: 다중 컨테이너 오케스트레이션 (PostgreSQL, Nginx, App)

---

## 주요 기능

### 1. **보험 등록 (Insurance Domain)**
- **보험 정보 등록**: 보험사, 상품명, 가입 연월 등 기본 정보 입력
- **세대 자동 판별**: 가입 연월 기반으로 실손보험 세대(1세대/2세대) 자동 판별
- **다중 보험 관리**: 여러 개의 보험을 동시에 등록하고 관리
- 보험사 및 상품 목록 조회

### 2. **약관 분석 (Analysis Domain)**
- **약관 PDF 업로드**: 보험 약관 PDF 파일을 AWS S3에 저장
- **텍스트 추출**: Apache PDFBox를 통한 정확한 텍스트 파싱
- **AI 기반 자동 분석**: Google Gemini AI로 복잡한 약관을 주요 보장 항목 기준으로 간단하게 요약
- **실시간 진행 상황 전송**: SSE(Server-Sent Events)로 분석 진행률을 실시간 업데이트
- **분석 이력 관리**: 모든 분석 결과 저장 및 언제든 조회 가능

### 3. **예상 환급금 계산 (Calculation Domain)**
- **의료비 정보 입력**: 의료 영수증 기반 진료 유형, 치료 항목, 금액 입력
- **보험 및 약관 선택**: 등록된 보험 또는 표준 약관 선택
- **보장 여부 확인**: 진료 유형, 항목별로 보장 여부를 즉시 확인
- **환급금 & 자가부담금 정확 계산**:
  - 실손 보험 세대별(1세대/2세대) 정규 약관 적용
  - 공제액(자기부담금), 한도액, 보장률 등 모든 요소 반영
  - 예상 환급액과 자가부담액을 명확하게 표시
- **계산 이력 저장**: 모든 계산 결과 자동 저장
- **자주 사용하는 결과 저장**: 즐겨찾기로 빠른 재참조 가능

### 4. **히스토리 관리**
- **등록한 보험**: 가입한 모든 보험 목록 조회, 수정, 삭제
- **분석 기록**: 분석한 약관 이력 전체 조회
- **계산 이력**: 과거 환급액 계산 결과 조회
- **상세 정보 확인**: 각 항목별 세부 내용 언제든 재확인 가능

### 5. **사용자 관리 (User Domain)**
- **카카오 소셜 로그인**: 편리한 회원가입 및 로그인
- **프로필 관리**: 사용자 정보 조회 및 닉네임 수정
- **안전한 인증**: JWT 기반 Access/Refresh 토큰 (토큰 로테이션)
- **회원 관리**: 로그아웃 및 회원 탈퇴

---

## DB / ERD

### 주요 테이블 구조

```sql
-- 사용자 테이블
users (id, social_id, name, email, profile_image_url, terms_agreed_at, deactivated_at, created_at, updated_at)

-- 리프레시 토큰 테이블
refresh_tokens (id, user_id, token, expires_at, created_at)

-- 보험 상품 마스터 테이블
insurances (id, company_name, product_name, contract_type, generation, coverage_structure,
            caution_point, pdf_file_url, pdf_file_name, core_summary)

-- 사용자 보험 등록 테이블
user_insurances (id, user_id, insurance_id, subscribed_at, has_non_covered_rider)

-- 약관 분석 이력 테이블
analysis_history (analysis_history_id, user_id, original_file_name, pdf_file_url,
                  company_name, product_name, contract_type, generation,
                  coverage_structure, caution_point, ai_summary, is_favorite,
                  is_deleted, deleted_at, created_at)

-- 계산 이력 테이블
calculation_history (calculation_history_id, user_id, insurance_id, medical_cost,
                     treatment_category, edi_code, is_covered, refund_amount,
                     deductible_amount, is_favorite, is_deleted, deleted_at, created_at)

-- 보장 룰 테이블
coverage_rule (coverage_rule_id, insurance_id, generation, edi_code, visit_type,
               treatment_category, purpose_type, is_covered, coverage_rate,
               deductible_amount, limit_amount, basis, disclaimer)

-- EDI 코드 테이블
edi_code (edi_code_id, code, treatment_name, fee_division_number, pay_type,
          unit_price, relative_value_point, effective_start_date, fee_type)
```

### 주요 관계도
- **User** (1) ─→ (N) **UserInsurance** (가입 보험)
- **User** (1) ─→ (N) **AnalysisHistory** (약관 분석 이력)
- **User** (1) ─→ (N) **CalculationHistory** (계산 이력)
- **Insurance** (1) ─→ (N) **UserInsurance** (사용자 보험 등록)
- **Insurance** (1) ─→ (N) **CoverageRule** (보장 룰)
- **CoverageRule** (N) ─→ (1) **Insurance** (보험 상품 참조)


## API 명세서

### Base URL
```
http://localhost:8080/api
```

### Swagger/OpenAPI Documentation
```
http://localhost:8080/swagger-ui.html
```

### 1. 인증 API (`/api/auth`)

#### 1.1 카카오 로그인
```
POST /api/auth/login
Content-Type: application/json

Request:
{
  "kakaoToken": "string"
}

Response:
{
  "code": "SUCCESS",
  "data": {
    "accessToken": "eyJhbGc...",
    "refreshToken": "eyJhbGc...",
    "isNewUser": true
  }
}
```

#### 1.2 토큰 재발급 (Refresh Token Rotation)
```
POST /api/auth/reissue
Content-Type: application/json

Request:
{
  "refreshToken": "string"
}

Response:
{
  "code": "SUCCESS",
  "data": {
    "accessToken": "eyJhbGc...",
    "refreshToken": "eyJhbGc..."
  }
}
```

#### 1.3 약관 동의
```
POST /api/auth/terms
Authorization: Bearer {accessToken}
Content-Type: application/json

Request:
{
  "ageOver14": true,
  "serviceTerms": true,
  "privacyPolicy": true
}

Response:
{
  "code": "SUCCESS"
}
```

#### 1.4 로그아웃
```
POST /api/auth/logout
Content-Type: application/json

Request:
{
  "refreshToken": "string"
}

Response:
{
  "code": "SUCCESS",
  "data": {
    "success": true
  }
}
```

#### 1.5 회원 탈퇴
```
DELETE /api/auth/withdraw
Authorization: Bearer {accessToken}

Response:
{
  "code": "SUCCESS"
}
```

### 2. 약관 분석 API (`/api`)

#### 2.1 SSE 연결
```
GET /api/sse/connect

Response: SSE Event Stream
event: connected
data: {clientId}

(분석 진행 시)
event: analysis-progress
data: {"status": "PROCESSING", ...}
```

#### 2.2 약관 분석 요청
```
POST /api/analysis
Authorization: Bearer {accessToken} (선택)
Content-Type: multipart/form-data

Form Data:
- clientId: string (필수)
- userInsuranceId: number (선택)
- file: MultipartFile (선택)

*userInsuranceId 또는 file 중 하나는 필수

Response:
{
  "code": "SUCCESS"
}
```

#### 2.3 분석 이력 조회
```
GET /api/history/analysis?page=0&size=5
Authorization: Bearer {accessToken}

Response:
{
  "code": "SUCCESS",
  "data": {
    "content": [
      {
        "analysisHistoryId": 1,
        "companyName": "삼성화재",
        "productName": "실손보험",
        "contractType": "개인실손",
        "generation": "2세대",
        "coverageStructure": "3대비급여",
        "cautionPoint": "갱신형",
        "isFavorite": false,
        "createdAt": "2024-01-01T10:00:00"
      }
    ],
    "pageInfo": {
      "page": 0,
      "size": 5,
      "totalPages": 1,
      "totalElements": 1
    }
  }
}
```

#### 2.4 분석 상세 조회
```
GET /api/history/analysis/{historyId}
Authorization: Bearer {accessToken}

Response:
{
  "code": "SUCCESS",
  "data": {
    "analysisHistoryId": 1,
    "originalFileName": "insurance_policy.pdf",
    "pdfFileUrl": "https://...",
    "companyName": "삼성화재",
    "productName": "실손보험",
    "contractType": "개인실손",
    "generation": "2세대",
    "coverageStructure": "3대비급여",
    "cautionPoint": "갱신형",
    "aiSummary": { ... }
  }
}
```

#### 2.5 분석 이력 삭제
```
DELETE /api/history/analysis/{historyId}
Authorization: Bearer {accessToken}

Response:
{
  "code": "SUCCESS"
}
```

#### 2.6 분석 이력 즐겨찾기 토글
```
PATCH /api/history/analysis/{historyId}
Authorization: Bearer {accessToken}

Response:
{
  "code": "SUCCESS"
}
```

### 3. 보험금 계산 API (`/api/calculations`)

#### 3.1 예상 환급액 계산
```
POST /api/calculations
Authorization: Bearer {accessToken}
Content-Type: application/json

Request:
{
  "insuranceId": "1",
  "medicalCost": 50000,
  "visitType": "OUTPATIENT",
  "treatmentCategory": "GENERAL",
  "purposeType": "TREATMENT",
  "hospitalType": "HOSPITAL",
  "payType": "PAY",
  "ediCode": "04B0001"
}

Response:
{
  "code": "SUCCESS",
  "data": {
    "isCovered": "PARTIAL_COVERED",
    "refundAmount": 35000,
    "deductibleAmount": 15000,
    "basis": "...",
    "deductibleBasis": "10,000원 공제 후 잔여 진료비의 90% 보장(잔여 자기부담 10%)",
    "fixedDeductibleAmount": 10000,
    "fixedDeductibleRate": 20,
    "disclaimer": "실제 금액은 보험사 약관에 따릅니다.",
    "treatmentInfos": ["외래", "일반", "급여"],
    "totalMedicalCost": 50000,
    "productName": "실손보험",
    "companyName": "삼성화재",
    "insuranceInfos": ["2세대", "3대비급여"],
    "joinDate": "2023-06",
    "deductibleRate": 30,
    "refundRate": 70,
    "ediCode": "04B0001"
  }
}
```

#### 3.2 계산 이력 조회
```
GET /api/calculations?page=0&size=20
Authorization: Bearer {accessToken}

Response:
{
  "code": "SUCCESS",
  "data": {
    "content": [
      {
        "id": 1,
        "calculationHistoryId": "1",
        "calculatedDate": "2024-01-01T00:00:00Z",
        "insuranceCoverage": "일반",
        "basis": ["일반"],
        "insuranceId": "1",
        "productName": "실손보험",
        "companyName": "삼성화재",
        "generation": "2",
        "joinDate": "2023-06",
        "ediCode": "04B0001",
        "medicalCost": 50000,
        "refundAmount": 35000,
        "isCovered": true,
        "isSaved": false
      }
    ],
    "pageInfo": {
      "page": 0,
      "size": 20,
      "totalPages": 1,
      "totalElements": 1
    }
  }
}
```

#### 3.3 계산 이력 삭제
```
DELETE /api/calculations/{calculationHistoryId}
Authorization: Bearer {accessToken}

Response:
{
  "code": "SUCCESS"
}
```

#### 3.4 계산 즐겨찾기 목록 조회
```
GET /api/calculations/favorites
Authorization: Bearer {accessToken}

Response:
{
  "code": "SUCCESS",
  "data": [
    {
      "id": 1,
      "calculationHistoryId": "1",
      "calculatedDate": "2024-01-01T00:00:00Z",
      "insuranceCoverage": "일반",
      "basis": ["일반"],
      "insuranceId": "1",
      "productName": "실손보험",
      "companyName": "삼성화재",
      "generation": "2",
      "joinDate": "2023-06",
      "ediCode": "04B0001",
      "medicalCost": 50000,
      "refundAmount": 35000,
      "isCovered": true,
      "isFavorite": true,
      "isSaved": true
    }
  ]
}
```

#### 3.5 계산 즐겨찾기 토글
```
PATCH /api/calculations/{calculationHistoryId}/favorite
Authorization: Bearer {accessToken}

Response:
{
  "code": "SUCCESS"
}
```

### 4. 보험 관리 API (`/api/insurance`)

#### 4.1 보험사 목록 조회
```
GET /api/insurance/companies

Response:
{
  "code": "SUCCESS",
  "data": {
    "companies": [
      {
        "id": "1",
        "name": "삼성화재"
      },
      {
        "id": "2",
        "name": "현대해상"
      }
    ]
  }
}
```

#### 4.2 세대 판별
```
POST /api/insurance/generation
Content-Type: application/json

Request:
{
  "companyId": "1",
  "joinDate": "2023-06"
}

Response:
{
  "code": "SUCCESS",
  "data": {
    "generation": 2
  }
}
```

#### 4.3 보험 등록
```
POST /api/insurance/register
Authorization: Bearer {accessToken}
Content-Type: application/json

Request:
{
  "insuranceId": 1,
  "subscribedAt": "2023-06"
}

Response:
{
  "code": "SUCCESS",
  "data": {
    "userInsuranceId": 1
  }
}
```

#### 4.4 내 보험 목록 조회
```
GET /api/insurance/list
Authorization: Bearer {accessToken}

Response:
{
  "code": "SUCCESS",
  "data": {
    "insurances": [
      {
        "userInsuranceId": 1,
        "companyName": "삼성화재",
        "productName": "실손보험",
        "generation": 2,
        "joinDate": "2023-06",
        "contractType": "개인실손",
        "coverageStructure": "3대비급여",
        "cautionPoint": "갱신형"
      }
    ]
  }
}
```

### 5. 사용자 관리 API (`/api/user`)

#### 5.1 프로필 조회
```
GET /api/user/me
Authorization: Bearer {accessToken}

Response:
{
  "code": "SUCCESS",
  "data": {
    "userId": 1,
    "name": "홍길동",
    "email": "user@example.com",
    "profileImageUrl": "https://..."
  }
}
```

#### 5.2 프로필 수정
```
PATCH /api/user/me
Authorization: Bearer {accessToken}
Content-Type: application/json

Request:
{
  "name": "새로운닉네임"
}

Response:
{
  "code": "SUCCESS"
}
```

### 공통 응답 형식

모든 API 응답은 다음의 통일된 형식을 따릅니다:

```json
{
  "code": "SUCCESS",
  "message": null,
  "data": { }
}
```

또는 에러 시:

```json
{
  "code": "ERROR",
  "message": "에러 메시지",
  "data": null
}
```

### 주요 HTTP 상태 코드

| Status | 설명 |
|--------|------|
| 200 OK | 요청 성공 |
| 400 Bad Request | 잘못된 요청 (검증 실패, 필수 파라미터 누락 등) |
| 401 Unauthorized | 인증 토큰 없음 또는 만료 |
| 403 Forbidden | 접근 권한 없음 |
| 404 Not Found | 리소스를 찾을 수 없음 |
| 500 Internal Server Error | 서버 내부 오류 |

---