package com.silsonfit.silsonfit_api.domain.auth.scheduler;

import com.silsonfit.silsonfit_api.domain.auth.repository.RefreshTokenRepository;
import com.silsonfit.silsonfit_api.domain.auth.service.AuthService;
import com.silsonfit.silsonfit_api.domain.insurance.repository.UserInsuranceRepository;
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
    private final UserInsuranceRepository userInsuranceRepository;

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
            // 등록 보험 삭제
            userInsuranceRepository.deleteAllByUserId(user.getId());

            // Refresh Token 삭제
            refreshTokenRepository.findByUser(user)
                    .ifPresent(refreshTokenRepository::delete);

            // 사용자 삭제
            userRepository.delete(user);

            log.info("사용자 삭제 완료: userId={}", user.getId());
        }

        log.info("탈퇴 유예 만료 사용자 {}명 삭제 완료", expiredUsers.size());
    }
}
