package com.silsonfit.silsonfit_api.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 항상 대기하는 스레드 개수
        executor.setCorePoolSize(5);

        // 대기열 큐 사이즈
        executor.setQueueCapacity(50);

        //최대 스레드 수 ( 대기열 큐마저 꽉 찼을 때 스레드를 늘린다 )
        executor.setMaxPoolSize(20);

        // 스레드 이름 접두사
        executor.setThreadNamePrefix("Silsonfit-");

        // 설정 적용 후 초기화
        executor.initialize();
        return executor;
    }
}
