package com.silsonfit.silsonfit_api.domain.analysis.service;

import com.silsonfit.silsonfit_api.domain.analysis.dto.AnalysisHistoryDetailResponse;
import com.silsonfit.silsonfit_api.domain.analysis.dto.AnalysisTaskDTO;
import com.silsonfit.silsonfit_api.domain.analysis.repository.AnalysisHistoryRepository;
import com.silsonfit.silsonfit_api.domain.analysis.vo.AnalysisResult;
import com.silsonfit.silsonfit_api.global.aws.S3Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalysisServiceTest {

    // 서비스가 의존하는 객체들을 Mock 객체로 생성
    @Mock
    AnalysisHistoryRepository analysisHistoryRepository;
    @Mock
    SseEmitters sseEmitters;
    @Mock
    PdfService pdfService;
    @Mock
    AiAnalysisService aiAnalysisService;
    @Mock
    S3Service s3Service;

    // Mock 객체들을 서비스에 주입
    @InjectMocks
    AnalysisService analysisService;

    @Test
    void PDF_업로드_AI분석_S3업로드_SSE전송_통합테스트() {
        Long userId = 1L;
        String clientId = "test-clientId";
        byte[] dummyBytes = "dummy pdf".getBytes();
        String contentType = "application/pdf";
        String originalFileName = "약관.pdf";

        AnalysisTaskDTO taskDTO = new AnalysisTaskDTO(userId, clientId, null, dummyBytes, contentType, originalFileName);

        // 테스트용 가짜분석 결과
        AnalysisResult.Metadata metadata = new AnalysisResult.Metadata("testCompany", "testProduct", "testContract",
                "3세대", "testCoverage", "testCaution");
        AnalysisResult dummyResult = new AnalysisResult(metadata, null);

        // Mock 객체 호출 시나리오
        when(s3Service.upload(dummyBytes, originalFileName, contentType))
                .thenReturn("https://s3-dummy-url.com/약관.pdf");
        when(pdfService.extractText(dummyBytes))
                .thenReturn("추출된 약관 텍스트");
        when(aiAnalysisService.analysisPrompt("추출된 약관 텍스트"))
                .thenReturn(dummyResult);

        analysisService.analysis(taskDTO);

        // s3 업로드 호출 검증
        verify(s3Service).upload(dummyBytes, originalFileName, contentType);

        // ai 분석 호출 검증
        verify(aiAnalysisService).analysisPrompt("추출된 약관 텍스트");

        // db 저장 호출 검증
        verify(analysisHistoryRepository).save(any());

        // 프론트로 sse 가 전송 됐는지 검증
        verify(sseEmitters).send(eq(clientId), eq("analysisComplete"), any(AnalysisHistoryDetailResponse.class));
        verify(sseEmitters).complete(clientId);
    }

}