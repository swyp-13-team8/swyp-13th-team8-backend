package com.silsonfit.silsonfit_api.domain.auth.scheduler;

import com.silsonfit.silsonfit_api.domain.auth.repository.RefreshTokenRepository;
import com.silsonfit.silsonfit_api.domain.auth.service.AuthService;
import com.silsonfit.silsonfit_api.domain.user.entity.User;
import com.silsonfit.silsonfit_api.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 탈퇴 유예 만료 사용자 정리 스케줄러
 *
 * 매일 자정에 실행하여 탈퇴 후 30일 경과한 사용자의 데이터를 삭제한다.
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class WithdrawCleanupScheduler {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    // TODO: 각 도메인 삭제 서비스 주입
    // private final AnalysisHistoryService analysisHistoryService;  // 분석 도메인 (C 담당)
    // private final CalcHistoryService calcHistoryService;          // 계산 도메인 (C 담당)
    // private final UserInsuranceRepository userInsuranceRepository; // 보험 도메인 (내 담당)

    /**
     * 매일 자정에 탈퇴 유예 만료 사용자 정리
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanupExpiredUsers() {
        LocalDateTime expiredBefore = LocalDateTime.now().minusDays(AuthService.WITHDRAW_GRACE_DAYS);
        List<User> expiredUsers = userRepository.findByDeactivatedAtNotNullAndDeactivatedAtBefore(expiredBefore);

        if (expiredUsers.isEmpty()) {
            return;
        }

        log.info("탈퇴 유예 만료 사용자 {}명 삭제 시작", expiredUsers.size());

        for (User user : expiredUsers) {
            // TODO: 각 도메인 데이터 삭제 (메서드 준비되면 연결)
            // analysisHistoryService.deleteByUserId(user.getId());  // 분석 도메인
            // calcHistoryService.deleteByUserId(user.getId());      // 계산 도메인
            // userInsuranceRepository.deleteByUserId(user.getId()); // 보험 도메인

            // Auth 도메인 데이터 삭제
            refreshTokenRepository.findByUser(user)
                    .ifPresent(refreshTokenRepository::delete);

            // 사용자 삭제
            userRepository.delete(user);

            log.info("사용자 삭제 완료: userId={}", user.getId());
        }

        log.info("탈퇴 유예 만료 사용자 {}명 삭제 완료", expiredUsers.size());
    }
}
