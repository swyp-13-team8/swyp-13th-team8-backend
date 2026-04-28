package com.silsonfit.silsonfit_api.domain.auth.service;

import com.silsonfit.silsonfit_api.domain.auth.client.KakaoClient;
import com.silsonfit.silsonfit_api.domain.auth.dto.LoginRequest;
import com.silsonfit.silsonfit_api.domain.auth.dto.LoginResponse;
import com.silsonfit.silsonfit_api.domain.auth.dto.TokenReissueRequest;
import com.silsonfit.silsonfit_api.domain.auth.dto.TokenReissueResponse;
import com.silsonfit.silsonfit_api.domain.auth.entity.RefreshToken;
import com.silsonfit.silsonfit_api.domain.auth.repository.RefreshTokenRepository;
import com.silsonfit.silsonfit_api.domain.user.entity.User;
import com.silsonfit.silsonfit_api.domain.user.repository.UserRepository;
import com.silsonfit.silsonfit_api.global.auth.JwtTokenProvider;
import com.silsonfit.silsonfit_api.global.error.BusinessException;
import com.silsonfit.silsonfit_api.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 인증 관련 비즈니스 로직
 *
 * 카카오 로그인, 토큰 재발급, 약관 동의 처리
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoClient kakaoClient;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 카카오 로그인 처리
     *
     * 최초 로그인 시 사용자 생성 + Access/Refresh Token 발급
     * 기존 사용자는 Refresh Token만 Rotation
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        // 1) 카카오에서 사용자 정보 조회
        KakaoClient.KakaoUserInfo kakaoUser =
                kakaoClient.getUserInfo(request.kakaoToken());

        // 2) socialId로 기존 사용자 조회, 없으면 신규 생성
        Optional<User> existing = userRepository.findBySocialId(kakaoUser.id());
        boolean isNewUser = existing.isEmpty();
        User user = existing.orElseGet(() -> userRepository.save(
                User.builder()
                        .socialId(kakaoUser.id())
                        .name(kakaoUser.name())
                        .email(kakaoUser.email())
                        .profileImageUrl(kakaoUser.profileImageUrl())
                        .build()
        ));

        // 3) 토큰 발급 및 Refresh Token 저장/갱신
        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshTokenValue = jwtTokenProvider.createRefreshToken();
        LocalDateTime expiresAt = calculateRefreshExpiresAt();

        saveOrUpdateRefreshToken(user, refreshTokenValue, expiresAt);

        return new LoginResponse(accessToken, refreshTokenValue, isNewUser);
    }

    /**
     * Access Token 재발급 (Refresh Token Rotation)
     *
     * 기존 Refresh Token 검증 후 Access/Refresh Token 모두 새로 발급
     * 기존 Refresh Token은 DB 갱신으로 무효화
     */
    @Transactional
    public TokenReissueResponse reissue(TokenReissueRequest request) {
        // 1) 저장된 Refresh Token 조회
        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(request.refreshToken())
                .orElseThrow(() -> new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        // 2) 만료 여부 확인
        if (refreshToken.isExpired()) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        // 3) 새 토큰 발급 및 Rotation
        User user = refreshToken.getUser();
        String newAccessToken = jwtTokenProvider.createAccessToken(user.getId());
        String newRefreshTokenValue = jwtTokenProvider.createRefreshToken();
        LocalDateTime expiresAt = calculateRefreshExpiresAt();

        refreshToken.updateToken(newRefreshTokenValue, expiresAt);

        return new TokenReissueResponse(newAccessToken, newRefreshTokenValue);
    }

    /**
     * 약관 동의 처리
     *
     * 신규 회원의 약관 동의를 저장하여 가입 완료 처리
     */
    @Transactional
    public void agreeTerms(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.getTermsAgreedAt() != null) {
            throw new BusinessException(ErrorCode.ALREADY_AGREED_TERMS);
        }

        user.agreeTerms();
    }

    /**
     * Refresh Token 저장 또는 기존 토큰 Rotation
     */
    private void saveOrUpdateRefreshToken(User user, String token, LocalDateTime expiresAt) {
        refreshTokenRepository.findByUser(user).ifPresentOrElse(
                existing -> existing.updateToken(token, expiresAt),
                () -> refreshTokenRepository.save(
                        RefreshToken.builder()
                                .user(user)
                                .token(token)
                                .expiresAt(expiresAt)
                                .build()
                )
        );
    }

    /**
     * 현재 시각 기준 Refresh Token 만료 시각 계산
     */
    private LocalDateTime calculateRefreshExpiresAt() {
        return LocalDateTime.now()
                .plus(Duration.ofMillis(jwtTokenProvider.getRefreshTokenExpiration()));
    }
}
