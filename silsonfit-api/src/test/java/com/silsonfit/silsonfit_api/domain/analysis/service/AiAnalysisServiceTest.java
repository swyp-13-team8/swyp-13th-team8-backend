package com.silsonfit.silsonfit_api.domain.analysis.service;

import com.silsonfit.silsonfit_api.domain.analysis.vo.AnalysisResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiAnalysisServiceTest {

    @Autowired
    AiAnalysisService aiAnalysisService;

    @Test
    void AI_약관분석_테스트() {
        String dummyPdfText = """
                무배당 삼성화재 다이렉트 실손의료비보험 (2601.6)
                가입대상: 개인실손, 4세대
                보장구조: 급여+비급여, 자기부담금 있음
                
                제1조 (기본보장)
                급여 입원비, 급여 통원 치료비, 급여 약값을 보장합니다.
                
                제2조 (특약)
                비급여 치료비, 3대 비급여(도수치료, 주사료, MRI)의 경우 별도 한도가 적용됩니다.
                
                제3조 (보상하지 않는 손해)
                임신, 출산, 건강검진, 예방접종, 해외 의료기관 치료비는 보상하지 않습니다.
                
                제4조 (갱신 및 청구)
                이 보험은 1년 갱신형이며 최대 5년 주기로 보장내용이 변경될 수 있습니다.
                청구는 서류 접수 후 3영업일 이내에 지급하는 것을 기본으로 합니다.
                """;

        System.out.println("AI 분석중..");
        long startTime = System.currentTimeMillis();

        AnalysisResult result = aiAnalysisService.analysisPrompt(dummyPdfText);

        long endTime = System.currentTimeMillis();
        System.out.println("분석 완료 - 소요 시간: " + (endTime - startTime) + "ms");

        System.out.println("=================[AI 분석결과]===========================");
        System.out.println(result.toString());
        System.out.println("=========================================================");
    }

}