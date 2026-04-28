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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * AuthService 단위 테스트
 *
 * Mockito로 의존성 격리한 순수 단위 테스트
 * 비즈니스 로직(신규/기존 사용자 분기, Refresh Token Rotation, 예외 분기) 검증
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final long REFRESH_TTL_MS = 1_209_600_000L; // 14일

    @Mock
    private KakaoClient kakaoClient;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    // ──────────── login ────────────

    @Test
    @DisplayName("신규 사용자 로그인 시 User와 RefreshToken을 저장하고 isNewUser=true로 응답한다")
    void login_newUser() {
        // given
        given(kakaoClient.getUserInfo("kakao-access")).willReturn(
                new KakaoClient.KakaoUserInfo(111L, "홍길동", "email@test.com", "http://img"));
        given(userRepository.findBySocialId(111L)).willReturn(Optional.empty());

        User savedUser = userWithId(1L, 111L);
        given(userRepository.save(any(User.class))).willReturn(savedUser);
        given(jwtTokenProvider.createAccessToken(1L)).willReturn("access-token");
        given(jwtTokenProvider.createRefreshToken()).willReturn("refresh-token");
        given(jwtTokenProvider.getRefreshTokenExpiration()).willReturn(REFRESH_TTL_MS);
        given(refreshTokenRepository.findByUser(savedUser)).willReturn(Optional.empty());

        // when
        LoginResponse response = authService.login(new LoginRequest("kakao-access"));

        // then
        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        assertThat(response.isNewUser()).isTrue();
        verify(userRepository).save(any(User.class));
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("기존 사용자 로그인 시 User 저장은 건너뛰고 RefreshToken을 Rotation 한다")
    void login_existingUser_rotation() {
        // given
        User user = userWithId(1L, 222L);
        given(kakaoClient.getUserInfo("kakao-access")).willReturn(
                new KakaoClient.KakaoUserInfo(222L, "홍길동", null, null));
        given(userRepository.findBySocialId(222L)).willReturn(Optional.of(user));
        given(jwtTokenProvider.createAccessToken(1L)).willReturn("access-token");
        given(jwtTokenProvider.createRefreshToken()).willReturn("new-refresh");
        given(jwtTokenProvider.getRefreshTokenExpiration()).willReturn(REFRESH_TTL_MS);

        RefreshToken existing = RefreshToken.builder()
                .user(user)
                .token("old-refresh")
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
        given(refreshTokenRepository.findByUser(user)).willReturn(Optional.of(existing));

        // when
        LoginResponse response = authService.login(new LoginRequest("kakao-access"));

        // then
        assertThat(response.isNewUser()).isFalse();
        assertThat(existing.getToken()).isEqualTo("new-refresh"); // Rotation 반영 확인
        verify(userRepository, never()).save(any(User.class));
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("기존 사용자인데 RefreshToken이 DB에 없으면 새로 save 한다")
    void login_existingUser_noRefresh_thenSave() {
        // given
        User user = userWithId(1L, 333L);
        given(kakaoClient.getUserInfo("kakao-access")).willReturn(
                new KakaoClient.KakaoUserInfo(333L, "홍길동", null, null));
        given(userRepository.findBySocialId(333L)).willReturn(Optional.of(user));
        given(jwtTokenProvider.createAccessToken(1L)).willReturn("access-token");
        given(jwtTokenProvider.createRefreshToken()).willReturn("refresh");
        given(jwtTokenProvider.getRefreshTokenExpiration()).willReturn(REFRESH_TTL_MS);
        given(refreshTokenRepository.findByUser(user)).willReturn(Optional.empty());

        // when
        authService.login(new LoginRequest("kakao-access"));

        // then
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    // ──────────── reissue ────────────

    @Test
    @DisplayName("재발급 성공 시 새 Access/Refresh 발급하고 기존 Refresh를 Rotation 한다")
    void reissue_success() {
        // given
        User user = userWithId(1L, 111L);
        RefreshToken stored = RefreshToken.builder()
                .user(user)
                .token("old-refresh")
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
        given(refreshTokenRepository.findByToken("old-refresh"))
                .willReturn(Optional.of(stored));
        given(jwtTokenProvider.createAccessToken(1L)).willReturn("new-access");
        given(jwtTokenProvider.createRefreshToken()).willReturn("new-refresh");
        given(jwtTokenProvider.getRefreshTokenExpiration()).willReturn(REFRESH_TTL_MS);

        // when
        TokenReissueResponse response =
                authService.reissue(new TokenReissueRequest("old-refresh"));

        // then
        assertThat(response.accessToken()).isEqualTo("new-access");
        assertThat(response.refreshToken()).isEqualTo("new-refresh");
        assertThat(stored.getToken()).isEqualTo("new-refresh"); // DB 엔티티 Rotation 반영
    }

    @Test
    @DisplayName("재발급 요청 토큰이 DB에 없으면 REFRESH_TOKEN_NOT_FOUND 예외")
    void reissue_tokenNotFound() {
        given(refreshTokenRepository.findByToken("missing")).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.reissue(new TokenReissueRequest("missing")))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
    }

    @Test
    @DisplayName("재발급 요청 토큰이 만료됐으면 REFRESH_TOKEN_EXPIRED 예외")
    void reissue_tokenExpired() {
        User user = userWithId(1L, 111L);
        RefreshToken expired = RefreshToken.builder()
                .user(user)
                .token("expired")
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .build();
        given(refreshTokenRepository.findByToken("expired")).willReturn(Optional.of(expired));

        assertThatThrownBy(() -> authService.reissue(new TokenReissueRequest("expired")))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.REFRESH_TOKEN_EXPIRED);
    }

    // ──────────── agreeTerms ────────────

    @Test
    @DisplayName("약관 동의 성공 시 termsAgreedAt이 설정된다")
    void agreeTerms_success() {
        // given
        User user = userWithId(1L, 111L);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        authService.agreeTerms(1L);

        // then
        assertThat(user.getTermsAgreedAt()).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 사용자의 약관 동의 시 USER_NOT_FOUND 예외")
    void agreeTerms_userNotFound() {
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.agreeTerms(999L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("이미 약관 동의한 사용자가 다시 요청 시 ALREADY_AGREED_TERMS 예외")
    void agreeTerms_alreadyAgreed() {
        // given
        User user = userWithId(1L, 111L);
        user.agreeTerms(); // 이미 동의
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> authService.agreeTerms(1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.ALREADY_AGREED_TERMS);
    }

    // ──────────── helper ────────────

    private User userWithId(Long id, Long socialId) {
        User user = User.builder()
                .socialId(socialId)
                .name("name")
                .build();
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }
}
