package com.silsonfit.silsonfit_api.domain.analysis.vo;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

/**
 * AI API 의 응답을 받을 수 있는 VO
 */
public record AnalysisResult(
        Metadata metadata,
        SummaryContent content
) {

    /**
     * PDF 표지 / 개요 등에서 추출한 보험 기본 정보 ( 보험이 없다면 이 정보를 사용함 )
     */
    public record Metadata(
            String companyName,         // 보험사
            String productName,         // 약관 이름
            @JsonPropertyDescription("계약 유형. 제시된 예시 중 반드시 1개만 단일 키워드로 선택 (예: 개인실손, 단체실손, 전환용, 재개용)")
            String contractType,        // 계약 유형
            @JsonPropertyDescription("실손의료보험의 세대. 제시된 예시 중 반드시 1개만 단일 키워드로 선택 (예: 1세대, 2세대, 3세대, 4세대, 5세대, 세대확인필요)")
            String generation,          // 세대
            @JsonPropertyDescription("보장 구조. 제시된 예시 중 반드시 1개만 단일 키워드로 선택 (예: 급여중심, 급여+비급여, 3대비급여, 비급여포함, 특약포함, 입.통원포함, 통원중심, 기본형중심, 처방조제포함")
            String coverageStructure,   // 보장 구조
            @JsonPropertyDescription("주의 포인트. 제시된 예시 중 반드시 1개만 단일 키워드로 선택 (예: 자기부담높음, 갱신형, 재가입형, 보장제한많음, 청구유의, 면책확인, 3대비급여주의, 상급병실주의")
            String cautionPoint         // 주의 포인트
    ) {}

    /**
     * 보험 상세 분석 결과
     */
    public record SummaryContent(
            @JsonPropertyDescription("AI 핵심 요약 (예: '급여(건강보험 적용 항목) 중심, '비급여는 특약 가입 시 보장', '1년 갱신형', '보장내용 변경주기 최대 5년')")
            List<String> coreSummary,           // AI 핵심 요약
            @JsonPropertyDescription("보장 구조 객체 (기본 보장, 추가 보장(특약 가입 시), 보장되지 않는 항목)")
            CoverageDetails coverageDetails,    // 보장 구조
            @JsonPropertyDescription("보장 범위 (예: '입원: 급여 시 약 80% 보장, 비급여 시 약 70% 보장', '통원: 최소 1~3만원 공제 후 지급, 또는 20~30%의 공제 중 더 큰 금액 차감')")
            List<String> coverageScope,         // 보장 범위
            @JsonPropertyDescription("제한 조건 (예: '통원 1회당 금액 제한', '통원, 3대 비급여 연간 횟수·금액 한도 (예: 도수치료, 주사 등)')")
            List<String> limitations,           // 제한 조건
            @JsonPropertyDescription("갱신 및 재가입 조건 (예: '1년 갱신형', '갱신 시 나이 증가나 손해율 등에 따라 보험료 인상 가능', '보장내용 변경주기 최대 5년', '비급여 특약은 이용량에 따라 보험료 차등제 적용')")
            List<String> renewalTerms,          // 갱신, 재가입
            @JsonPropertyDescription("청구 방법 (예: '서류 접수 후 3영업일 내 지급 (기본)', '초가 조사 필요 시 최대 30영업일 소요')")
            List<String> claimMethod,           // 청구 방법
            @JsonPropertyDescription("해지 및 환급 안내 (예: '1년 단기 순수보장성, 해약환급금이 발생하지 않음', '보장 목적의 소멸성 보험')")
            List<String> cancellationAndRefund  // 해지, 환급
    ) {}

    public record CoverageDetails(
            @JsonPropertyDescription("기본 보장 항목 (예: '급여 입원비', '급여 통원 치료비', '급여 약값')")
            List<String> basicCoverages,    // 기본 보장
            @JsonPropertyDescription("추가 보장 및 특약 가입 시 항목 (예: '비급여 치료비', '3대 비급여(도수치료·체외충격파·증식치료 / 주사료 / MRI·MRA)의 경우 별도 한도, 조건 적용')")
            List<String> specialCoverages,  // 추가 보장 (특약 가입 시)
            @JsonPropertyDescription("보장되지 않는 항목 및 면책 조항 (예: '임신·출산, 건강검진, 예방접종, 일부 치과·한방 비급여, 자동차/산재보험 처리 의료비, 해외 의료기관 치료비 등')")
            List<String> exclusions         // 보장되지 않는 항목
    ) {
    }
}
