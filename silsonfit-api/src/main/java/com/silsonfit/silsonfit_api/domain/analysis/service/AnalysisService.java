package com.silsonfit.silsonfit_api.domain.analysis.service;

import com.silsonfit.silsonfit_api.domain.analysis.dto.AnalysisHistoryCreateCommand;
import com.silsonfit.silsonfit_api.domain.analysis.dto.AnalysisHistoryDetailResponse;
import com.silsonfit.silsonfit_api.domain.analysis.dto.AnalysisTaskDTO;
import com.silsonfit.silsonfit_api.domain.analysis.entity.AnalysisHistory;
import com.silsonfit.silsonfit_api.domain.analysis.repository.AnalysisHistoryRepository;
import com.silsonfit.silsonfit_api.domain.analysis.vo.AnalysisResult;
import com.silsonfit.silsonfit_api.domain.insurance.dto.InsuranceInfoDto;
import com.silsonfit.silsonfit_api.domain.insurance.service.InsuranceService;
import com.silsonfit.silsonfit_api.global.aws.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisService {

    private final AnalysisHistoryRepository analysisHistoryRepository;
    private final SseEmitters sseEmitters;
    private final PdfService pdfService;
    private final AiAnalysisService aiAnalysisService;
    private final S3Service s3UploadService;
    private final InsuranceService insuranceService;

    /**
     * 사용자가 요청한 보험 약관 PDF 파일을 비동기 스레드에서 분석 (@Async)
     *
     * @param dto userId, clientId, userInsuranceId, 파일정보가 있는 dto
     */
    @Async("taskExecutor")
    public void analysis(AnalysisTaskDTO dto) {
        log.info("약관 분석 Service start - userId={}", dto.userId());

        InsuranceInfoDto insuranceInfo = null;
        String pdfFileUrl;
        String originalFileName;
        String extractedText;

        try {
            // 1. 내 보험을 선택해서 분석 할 경우
            if (dto.userInsuranceId() != null) {
                log.info("내 보험 선택 후 약관 분석 - userInsuranceId={}", dto.userInsuranceId());

                // 1. 내 보험 ID로 InsuranceInfoDto 객체를 가져온다.
                insuranceInfo = insuranceService.getInsuranceInfo(dto.userInsuranceId());
                originalFileName = insuranceInfo.pdfFileName();
                pdfFileUrl = insuranceInfo.pdfFileUrl();

                // 2. PDF 텍스트 추출
                extractedText = pdfService.extractText(pdfFileUrl);
            }
            // 2. 내 컴퓨터에서 PDF 파일을 업로드하여 분석할 경우
            else {
                log.info("내 컴퓨터에서 PDF 파일을 업로드 하여 분석 - fileName={}", dto.originalFileName());
                originalFileName = dto.originalFileName();

                // 1. 파일을 S3에 업로드 후 URL 반환
                pdfFileUrl = s3UploadService.upload(dto.fileBytes(), dto.originalFileName(), dto.contentType());

                // 2. PDF 파일 텍스트 추출
                extractedText = pdfService.extractText(dto.fileBytes());
            }

            // 3. AI 분석
            AnalysisResult result = aiAnalysisService.analysisPrompt(extractedText);

            // 4. 회원이면 DB에 저장
            if (dto.userId() != null) {
                // 스냅샷 정보를 보험 엔티티에서 꺼내어 넣어준다.
                AnalysisHistoryCreateCommand command;
                if (dto.userInsuranceId() != null) {

                    // insuranceInfo 의 generation 이 int 타입이기 때문에 String 으로 변환
                    String generation;
                    switch (insuranceInfo.generation()) {
                        case 1 -> generation = "1세대";
                        case 2 -> generation = "2세대";
                        case 3 -> generation = "3세대";
                        case 4 -> generation = "4세대";
                        case 5 -> generation = "5세대";
                        default -> generation = "세대확인필요";
                    }
                            command = new AnalysisHistoryCreateCommand(dto.userId(), originalFileName, pdfFileUrl,
                            insuranceInfo.companyName(), insuranceInfo.productName(), insuranceInfo.contractType().getDisplayName(),
                            generation, insuranceInfo.coverageStructure().getDisplayName(),
                            insuranceInfo.cautionPoint().getDisplayName(), result);
                }
                // 스냅샷 정보를 AI 분석결과에서 꺼내어 넣어준다.
                else {
                    command = new AnalysisHistoryCreateCommand(dto.userId(), originalFileName, pdfFileUrl,
                            result.metadata().companyName(), result.metadata().productName(), result.metadata().contractType(),
                            result.metadata().generation(), result.metadata().coverageStructure(), result.metadata().cautionPoint(), result);
                }
                // DB에 저장
                saveHistory(command);
                log.info("분석 이력 저장 성공 - userId={}", command.userId());
            }

            // 5. 프론트에 응답보낼 dto 조립
            AnalysisHistoryDetailResponse response =
                    AnalysisHistoryDetailResponse.of(result, originalFileName, pdfFileUrl);

            // 6. 프론트에 sse 전송
            sseEmitters.send(dto.clientId(), "analysisComplete", response);
            sseEmitters.complete(dto.clientId());
        } catch (Exception e) {
            log.error("약관 분석 중 오류 발생 - clientId={}", dto.clientId(), e);

            // 에러 발생 시 프론트가 무한 대기 하지 않도록 에러 이벤트를 전송해준다.
            sseEmitters.send(dto.clientId(), "error", "약관 분석 중 오류 발생");
            sseEmitters.complete(dto.clientId());
        }
        log.info("약관 분석 Service 종료");
    }

    /**
     * AI 분석 후, 회원이라면 분석 이력을 DB에 저장한다.
     * AI 분석은 비동기처리를 하기때문에 DB 저장 로직만 따로 빼서 트랜잭션을 적용함.
     *
     * @param command DB에 저장할 데이터가 담긴 DTO
     */
    @Transactional
    public void saveHistory(AnalysisHistoryCreateCommand command) {
        AnalysisHistory history = AnalysisHistory.create(command);
        analysisHistoryRepository.save(history);
    }
}
