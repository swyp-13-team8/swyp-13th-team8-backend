package com.silsonfit.silsonfit_api.domain.analysis.service;

import com.silsonfit.silsonfit_api.domain.analysis.vo.AnalysisResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@SpringBootTest
public class AiAnalysisIntegrationTest {

    @Autowired
    AiAnalysisService aiAnalysisService;

    @Autowired
    PdfService pdfService;

    @Test
    void PDF_파싱후_AI_분석_통합테스트() throws IOException {
        File pdfFile = new File("C:\\Java\\무배당 메리츠 실손의료비보험1810약관.pdf");

        MockMultipartFile mockFile = new MockMultipartFile(
                "file", pdfFile.getName(), "application/pdf", Files.readAllBytes(pdfFile.toPath()));

        // pdf 에서 텍스트 추출
        System.out.println("PDF 텍스트 추출중");
        String extractText = pdfService.extractText(mockFile.getBytes());
        System.out.println("추출 완료 - 텍스트 길이 = " + extractText.length());

        // ai 분석
        System.out.println("AI 분석 start");
        long startTime = System.currentTimeMillis();
        AnalysisResult result = aiAnalysisService.analysisPrompt(extractText);
        long endTime = System.currentTimeMillis();

        System.out.println("분석완료 - 소요시간 : " + (endTime - startTime) + "ms");

        System.out.println("=================[AI 분석결과]===========================");
        System.out.println(result.toString());
        System.out.println("=========================================================");
    }
}
