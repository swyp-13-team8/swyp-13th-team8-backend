package com.silsonfit.silsonfit_api.global.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        // 스프링이 자동 제공하는 Builder 를 받아, 기본 ChatClient 를 Bean 으로 등록
        return builder.build();
    }
}
