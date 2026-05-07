package com.silsonfit.silsonfit_api.domain.insurance.entity;

import com.silsonfit.silsonfit_api.domain.insurance.enums.CautionPoint;
import com.silsonfit.silsonfit_api.domain.insurance.enums.ContractType;
import com.silsonfit.silsonfit_api.domain.insurance.enums.CoverageStructure;
import com.silsonfit.silsonfit_api.global.common.converter.StringListConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 보험 상품 마스터 엔티티
 *
 * 빅5 손보사 인기 상품 정보를 관리한다.
 */
@Entity
@Table(name = "insurances")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Insurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 보험사명 (삼성화재, 현대해상, DB손해보험, KB손해보험, 메리츠화재)
    @Column(nullable = false)
    private String companyName;

    // 보험 상품명
    @Column(nullable = false)
    private String productName;

    // 계약 유형
    @Enumerated(EnumType.STRING)
    @Column
    private ContractType contractType;

    // 세대 (1~4)
    @Column(nullable = false)
    private int generation;

    // 보장 구조
    @Enumerated(EnumType.STRING)
    @Column
    private CoverageStructure coverageStructure;

    // 주의 사항
    @Enumerated(EnumType.STRING)
    @Column
    private CautionPoint cautionPoint;

    // 약관 PDF S3 경로
    @Column
    private String pdfFileUrl;

    // 약관 PDF 원본 파일명
    @Column
    private String pdfFileName;

    // AI 핵심 요약
    @Convert(converter = StringListConverter.class)
    @Column(length = 1000)
    private List<String> coreSummary;

    @Builder
    public Insurance(String companyName, String productName, ContractType contractType,
                     int generation, CoverageStructure coverageStructure, CautionPoint cautionPoint,
                     String pdfFileUrl, String pdfFileName, List<String> coreSummary) {
        this.companyName = companyName;
        this.productName = productName;
        this.contractType = contractType;
        this.generation = generation;
        this.coverageStructure = coverageStructure;
        this.cautionPoint = cautionPoint;
        this.pdfFileUrl = pdfFileUrl;
        this.pdfFileName = pdfFileName;
        this.coreSummary = coreSummary;
    }

    /**
     * 계약 유형 라벨 (예: "개인실손")
     */
    public String getContractTypeLabel() {
        return contractType != null ? contractType.getDisplayName() : null;
    }

    /**
     * 보장 구조 라벨 (예: "3대비급여")
     */
    public String getCoverageStructureLabel() {
        return coverageStructure != null ? coverageStructure.getDisplayName() : null;
    }

    /**
     * 주의 포인트 라벨 (예: "갱신형")
     */
    public String getCautionPointLabel() {
        return cautionPoint != null ? cautionPoint.getDisplayName() : null;
    }
}
