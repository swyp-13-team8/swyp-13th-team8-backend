package com.silsonfit.silsonfit_api.domain.analysis.service;

import com.silsonfit.silsonfit_api.global.error.BusinessException;
import com.silsonfit.silsonfit_api.global.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

@Service
@Slf4j
public class PdfService {

    /**
     * PDF 파일의 텍스트를 추출하여 반환
     *
     * @param fileBytes PDF 파일의 byte 배열
     * @return 추출된 전체 텍스트
     */
    public String extractText(byte[] fileBytes) {
        log.info("[FILE] 텍스트 추출 start");

        // try-with-resources 구문을 이용하여 finally 없이 자동으로 자원 해제 처리
        try (PDDocument document = Loader.loadPDF(fileBytes)) {

            // PDF 를 추출하기 위한 객체 생성
            PDFTextStripper stripper = new PDFTextStripper();

            String extractedText = stripper.getText(document);

            log.info("[FILE] 텍스트 추출 완료 - length={}", extractedText.length());
            return extractedText;

        } catch (IOException e) {
            log.error("[FILE] PDF 텍스트 추출 실패 - {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.PDF_PARSING_ERROR);
        }
    }

    /**
     * S3 URL 의 파일을 열어 텍스트 추출
     *
     * @param pdfFileUrl PDF 가 저장되어있는 URL
     * @return 추출된 전체 텍스트
     */
    // S3에서 가져온 URL 로 PDF 를 가져와 텍스트 추출
    public String extractText(String pdfFileUrl) {
        log.info("[URL] 텍스트 추출 start");
        // URL 에서 직접 InputStream 을 열어 PDFBox 에 넘겨준다.
        URI uri = UriComponentsBuilder
                .fromUriString(pdfFileUrl)
                .build()
                .encode()
                .toUri();
        try (InputStream in = uri.toURL().openStream();
             PDDocument document = Loader.loadPDF(in.readAllBytes())) {

            PDFTextStripper stripper = new PDFTextStripper();

            String extractedText = stripper.getText(document);

            log.info("[URL] 텍스트 추출 완료 - length={}", extractedText.length());
            return extractedText;
        } catch (IOException e) {
            log.error("[URL] PDF 텍스트 추출 실패 - {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.PDF_PARSING_ERROR);
        }
    }
}
