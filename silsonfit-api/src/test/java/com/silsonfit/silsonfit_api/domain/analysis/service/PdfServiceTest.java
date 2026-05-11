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
    @Test
    void 한글_파일명_S3_URL_텍스트_추출_테스트() {
        String pdfUrl = "https://silsonfit-storage.s3.ap-northeast-2.amazonaws.com/insurances/무배당 프로미라이프 실손의료비보험1701_약관.pdf";

        String s = pdfService.extractText(pdfUrl);

        System.out.println("=================[파싱된 텍스트 결과]======================");
        System.out.println(s.substring(0, 20000));
        System.out.println("=========================================================");
    }

}