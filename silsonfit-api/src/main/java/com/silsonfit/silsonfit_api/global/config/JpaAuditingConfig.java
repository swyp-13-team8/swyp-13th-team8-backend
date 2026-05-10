package com.silsonfit.silsonfit_api.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Spring Data JPA 의 Auditing(시간 자동 매핑) 기능을 활성화하는 설정 클래스
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
