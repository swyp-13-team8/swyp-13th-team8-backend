package com.silsonfit.silsonfit_api.domain.analysis.entity;

import com.silsonfit.silsonfit_api.domain.analysis.dto.AnalysisResultDTO;
import com.silsonfit.silsonfit_api.global.common.BaseCreatedTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * AI의 약관 분석 결과 이력을 저장하는 엔티티
 */
@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnalysisHistory extends BaseCreatedTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_history_id")
    private Long id;

    // 유저 id만 필요하기 때문에 객체 참조 x
    @Column(name = "user_id")
    private Long userId;

    // 1. 파일 정보
    @Column(name = "original_file_name", nullable = false)
    private String originalFileName; // 삼성화재_약관.pdf

    @Column(name = "pdf_file_url", nullable = false, length = 1000)
    private String pdfFileUrl; // S3 스토리지 URL

    // 2. 보험 관련 정보 스냅샷
    @Column(name = "company_name")
    private String companyName; // 보험사

    @Column(name = "product_name")
    private String productName; // 약관 이름

    @Column(name = "contract_type")
    private String contractType; // 계약 유형

    @Column(name = "generation")
    private String generation; // 보험 세대

    @Column(name = "coverage_structure")
    private String coverageStructure; // 보장 구조

    @Column(name = "caution_point")
    private String cautionPoint; // 주의 포인트

    // 3. AI 분석 결과
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ai_summary", columnDefinition = "jsonb")
    private AnalysisResultDTO aiSummary;

    // 4. 추가 기능
    @Column(name = "is_favorite", nullable = false)
    @Builder.Default
    private Boolean isFavorite = false; // 즐겨찾기

    // 생성자 - 정적 팩토리 메서드 패턴 + 빌더 사용
    public static AnalysisHistory create(Long userId, String originalFileName, String pdfFileUrl,
                                         String companyName, String productName, String contractType,
                                         String generation, String coverageStructure, String cautionPoint,
                                         AnalysisResultDTO aiSummary) {
        return AnalysisHistory.builder()
                .userId(userId)
                .originalFileName(originalFileName)
                .pdfFileUrl(pdfFileUrl)
                .companyName(companyName)
                .productName(productName)
                .contractType(contractType)
                .generation(generation)
                .coverageStructure(coverageStructure)
                .cautionPoint(cautionPoint)
                .aiSummary(aiSummary)
                .build();
    }

    // 즐겨찾기 ON / OFF
    public void toggleFavorite() {
        this.isFavorite = !this.isFavorite;
    }
}
