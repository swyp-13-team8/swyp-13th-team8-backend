package com.silsonfit.silsonfit_api.domain.analysis.controller;

import com.silsonfit.silsonfit_api.domain.analysis.dto.AnalysisRequest;
import com.silsonfit.silsonfit_api.domain.analysis.dto.AnalysisTaskDTO;
import com.silsonfit.silsonfit_api.domain.analysis.service.AnalysisService;
import com.silsonfit.silsonfit_api.domain.analysis.service.SseEmitters;
import com.silsonfit.silsonfit_api.global.auth.CustomUserDetails;
import com.silsonfit.silsonfit_api.global.common.ApiResponse;
import com.silsonfit.silsonfit_api.global.error.BusinessException;
import com.silsonfit.silsonfit_api.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class AnalysisController {

    private final AnalysisService analysisService;
    private final SseEmitters sseEmitters;

    /**
     * 클라이언트와 서버간의 SSE 연결
     * 연결이 성공하면 프론트에게 clientId를 발송한다.
     *
     * @param userDetails 사용자 정보 (비회원은 null)
     * @return 연결이 유지되는 SseEmitter 객체
     */
    @GetMapping(value = "/sse/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // 비회원은 랜덤 UUID 값을 clientId로 사용
        String clientId = (userDetails != null) ? String.valueOf(userDetails.getUserId())
                : UUID.randomUUID().toString();

        SseEmitter emitter = sseEmitters.subscribe(clientId);

        // connected 라는 이름으로 프론트쪽에 clientId를 넘겨준다.
        sseEmitters.send(clientId, "connected", clientId);
        return emitter;
    }

    /**
     * 약관 PDF 파일 또는 내 보험 ID를 받아 비동기 AI 분석
     *
     * @param userDetails 사용자 정보 (비회원은 null)
     * @param request SSE 클라이언트 ID, UserInsuranceId or PDF File
     */
    @PostMapping(value = "/analysis", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Void> analysis(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @ModelAttribute AnalysisRequest request) {

        // 요청 유효성 검사
        if (!request.isValid()) {
            log.warn("보험 ID나 PDF 파일 중 하나는 필수입니다.");
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        // 회원이면 userId를 채워주고, 비회원이면 null
        Long userId = (userDetails != null) ? userDetails.getUserId() : null;

        // 파일 여부
        boolean hasFile = (request.file() != null && !request.file().isEmpty());

        log.info("약관 분석 요청 - userId={}, clientId={}, userInsuranceId={}, hasFile={}",
                userId, request.clientId(), request.userInsuranceId(),
                hasFile);

        byte[] fileBytes = null;
        String contentType = null;
        String originalFileName = null;
        if (hasFile) {
            try {
                fileBytes = request.file().getBytes();
                contentType = request.file().getContentType();
                originalFileName = request.file().getOriginalFilename();
            } catch (IOException e) {
                log.error("파일을 읽는 중 오류가 발생했습니다.", e);
                throw new BusinessException(ErrorCode.FILE_READ_FAILED);
            }
        }

        AnalysisTaskDTO analysisTaskDTO = new AnalysisTaskDTO(userId, request.clientId(), request.userInsuranceId(),
                fileBytes, contentType, originalFileName);

        analysisService.analysis(analysisTaskDTO);

        return ApiResponse.success();
    }
}
