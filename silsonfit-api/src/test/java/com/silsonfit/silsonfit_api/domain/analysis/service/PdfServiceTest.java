package com.silsonfit.silsonfit_api.domain.analysis.service;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

class PdfServiceTest {

    PdfService pdfService = new PdfService();

    @Test
    void 텍스트_추출_테스트() throws IOException {
        File pdfFile = new File("C:\\Java\\공공데이터 제공및이용업무운영지침.pdf");

        byte[] content = Files.readAllBytes(pdfFile.toPath());
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                pdfFile.getName(),
                "application/pdf",
                content
        );

        String result = pdfService.extractText(mockFile.getBytes());

        System.out.println("=================[파싱된 텍스트 결과]======================");
        System.out.println(result.substring(0, 21700));
        System.out.println("=========================================================");

    }

}