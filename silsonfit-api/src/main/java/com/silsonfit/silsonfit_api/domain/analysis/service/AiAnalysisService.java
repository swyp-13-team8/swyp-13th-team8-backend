package com.silsonfit.silsonfit_api.domain.analysis.service;

import com.silsonfit.silsonfit_api.domain.analysis.vo.AnalysisResult;
import com.silsonfit.silsonfit_api.global.error.BusinessException;
import com.silsonfit.silsonfit_api.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiAnalysisService {

    private final ChatClient chatClient;

    /**
     * 추출된 약관 PDF 텍스트를 AI 모델에 전송하여 분석 결과를 반환한다.
     *
     * @param pdfText PDF 파일에서 추출한 원본 약관 텍스트
     * @return AI가 분석 후 요약한 정보를 담은 VO
     */
    public AnalysisResult analysisPrompt(String pdfText) {
        log.info("AI 약관 분석 start - length={}", pdfText.length());

        BeanOutputConverter<AnalysisResult> outputConverter = new BeanOutputConverter<>(AnalysisResult.class);
        String format = outputConverter.getFormat();

        String prompt = """
                너는 10년 차 수석 손해사정사이자 약관 분석 전문가야.
                제공된 보험 약관 텍스트를 분석해서, 사용자가 웹의 한 화면에서 빠르게 읽을 수 있도록 핵심만 압축한 요약 리포트를 만들어줘.
                
                [분석 및 작성 가이드]
                1. 분량 압축: 약관 내용이 방대하더라도, 각 항목의 배열(Array)에 들어가는 문장은 핵심만 추려서 최대한 짧고 간결하게 1~2줄 이내로 작성해. 불필요한 부연 설명은 모두 제거해.
                2. 문체(말투) 변경: '~합니다', '~습니다', '~입니다' 등의 길고 격식 있는 서술어는 절대 사용하지마. 대신 명사로 문장을 끝맺거나, '~함', '~됨', '~임'으로 끝나는 [개조식 문체]를 엄격하게 적용해.
                3. 예시 활용 원칙: 제공된 JSON 포맷의 예시 문장들은 '이런 식의 짧은 문체를 사용하라'는 참고용이야. 예시 내용을 절대 그대로 복사하지 말고, 반드시 [약관 원본 텍스트]의 팩트에 기반해서 새롭게 요약해.
                4. 표나 리스트가 텍스트로 평면화되어 있으니 문맥을 잘 파악해서 알맞은 항목에 매핑해.
                5. 약관에 해당 내용이 전혀 없다면 임의로 지어내지 말고 빈 배열([])이나 "내용 없음"으로 처리해.
                
                [주의 사항]
                - 반드시 아래에 제공된 JSON 스키마 포맷에 완벽하게 일치하는 JSON만 출력해.
                - JSON 외의 어떠한 부연 설명(```json 등)도 포함하지 마.
                
                [출력 포맷]
                {format}
                
                [약관 원본 텍스트]
                {text}
                """;

        try {
            String aiResponse = chatClient.prompt()
                    .user(u -> u.text(prompt)
                            .param("format", format)
                            .param("text", pdfText))
                    .call()
                    .content();

            log.info("AI 약관 분석 성공 - JSON 응답 반환");
            return outputConverter.convert(aiResponse);
        } catch (Exception e) {
            log.error("AI 분석 중 예외 발생 - {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.AI_ANALYSIS_FAILED);
        }
    }
}
