package com.silsonfit.silsonfit_api.domain.analysis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SseEmitters {

    // 여러 클라이언트가 동시에 분석하기를 눌렀을 때 비동기 처리를 하기 때문에
    // 여러 스레드에서 동시에 접근한다. 그래서 ConcurrentHashMap 을 사용해야한다.
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * 클라이언트의 SSE 연결을 생성, Map 에 저장한다.
     * 연결 정상 종료, 타임 아웃 시 콜백 설정 포함
     *
     * @param clientId 클라이언트 ID (userID 또는 UUID)
     */
    public SseEmitter subscribe(String clientId) {
        SseEmitter sseEmitter = new SseEmitter(60L * 5 * 1000); // 5분 타임아웃
        emitters.put(clientId, sseEmitter);
        log.info("sse 연결 성공 - clientId={}", clientId);

        // 콜백 설정
        sseEmitter.onCompletion(()->{
            log.info("sse 연결 정상 종료 - clientId={}", clientId);
            emitters.remove(clientId);
        });
        sseEmitter.onTimeout(()->{
            log.info("sse 연결 타임아웃 - clientId={}", clientId);
            emitters.remove(clientId);
        });
        return sseEmitter;
    }

    /**
     * 특정 클라이언트에게 실시간 데이터 전송
     *
     * @param clientId 클라이언트 ID (userID 또는 UUID)
     * @param name 프론트에서 수신할 데이터의 이름
     * @param data 전송할 데이터 객체
     */
    public void send(String clientId, String name, Object data) {
        SseEmitter sseEmitter = emitters.get(clientId);
        if (sseEmitter != null) {
            try {
                sseEmitter.send(SseEmitter.event()
                        .name(name)
                        .data(data));
            } catch (IOException e) {
                log.error("SSE 데이터 전송 중 에러 - clientId={}", clientId);
                emitters.remove(clientId);
            }
        } else {
            log.info("클라이언트 아이디에 해당하는 SSE 연결이 존재하지 않습니다. - clientId={}", clientId);
        }
    }

    /**
     * SSE 연결을 명시적으로 종료
     *
     * @param clientId 클라이언트 ID (userID 또는 UUID)
     */
    public void complete(String clientId) {
        SseEmitter sseEmitter = emitters.get(clientId);
        if (sseEmitter != null) {
            sseEmitter.complete();
            emitters.remove(clientId);
        }
    }
}
