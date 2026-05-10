package com.silsonfit.silsonfit_api.global.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JwtTokenProvider 단위 테스트
 *
 * Spring 컨텍스트 없이 순수 단위 테스트로 검증한다.
 */
class JwtTokenProviderTest {

    private static final String SECRET = "test-secret-for-unit-test-minimum-32-characters-long";
    private static final long ACCESS_TOKEN_EXPIRATION = 3_600_000L;      // 60분
    private static final long REFRESH_TOKEN_EXPIRATION = 1_209_600_000L; // 14일

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(
                SECRET, ACCESS_TOKEN_EXPIRATION, REFRESH_TOKEN_EXPIRATION);
    }

    @Test
    @DisplayName("Access Token을 생성하면 subject의 userId를 정상적으로 추출할 수 있다")
    void createAccessTokenAndGetUserId() {
        Long userId = 1L;

        String token = jwtTokenProvider.createAccessToken(userId);
        Long extracted = jwtTokenProvider.getUserId(token);

        assertThat(extracted).isEqualTo(userId);
    }

    @Test
    @DisplayName("유효한 Access Token은 검증에 통과한다")
    void validateValidToken() {
        String token = jwtTokenProvider.createAccessToken(1L);

        boolean valid = jwtTokenProvider.validateToken(token);

        assertThat(valid).isTrue();
    }

    @Test
    @DisplayName("페이로드가 변조된 토큰은 서명 불일치로 검증에 실패한다")
    void validateTamperedToken() {
        String token = jwtTokenProvider.createAccessToken(1L);

        // 페이로드 영역(첫 번째 '.' 바로 뒤)의 한 글자를 다른 문자로 변조
        int firstDot = token.indexOf('.');
        char[] chars = token.toCharArray();
        chars[firstDot + 1] = (chars[firstDot + 1] == 'A') ? 'B' : 'A';
        String tampered = new String(chars);

        boolean valid = jwtTokenProvider.validateToken(tampered);

        assertThat(valid).isFalse();
    }

    @Test
    @DisplayName("다른 secret으로 서명된 토큰은 검증에 실패한다")
    void validateTokenSignedByDifferentSecret() {
        JwtTokenProvider other = new JwtTokenProvider(
                "different-secret-for-test-minimum-32-characters",
                ACCESS_TOKEN_EXPIRATION,
                REFRESH_TOKEN_EXPIRATION);
        String otherToken = other.createAccessToken(1L);

        boolean valid = jwtTokenProvider.validateToken(otherToken);

        assertThat(valid).isFalse();
    }

    @Test
    @DisplayName("만료된 토큰은 검증에 실패한다")
    void validateExpiredToken() throws InterruptedException {
        // 만료 시간을 1ms로 설정한 Provider
        JwtTokenProvider shortLived = new JwtTokenProvider(
                SECRET, 1L, REFRESH_TOKEN_EXPIRATION);
        String token = shortLived.createAccessToken(1L);

        // 만료 대기
        Thread.sleep(10);

        boolean valid = shortLived.validateToken(token);

        assertThat(valid).isFalse();
    }

    @Test
    @DisplayName("JWT 형식이 아닌 문자열은 검증에 실패한다")
    void validateMalformedToken() {
        boolean valid = jwtTokenProvider.validateToken("not.a.jwt");

        assertThat(valid).isFalse();
    }

    @Test
    @DisplayName("Refresh Token은 호출마다 서로 다른 값을 생성한다")
    void createRefreshTokenIsUnique() {
        String token1 = jwtTokenProvider.createRefreshToken();
        String token2 = jwtTokenProvider.createRefreshToken();

        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    @DisplayName("Refresh Token 만료 시간(ms)을 반환한다")
    void getRefreshTokenExpiration() {
        long expiration = jwtTokenProvider.getRefreshTokenExpiration();

        assertThat(expiration).isEqualTo(REFRESH_TOKEN_EXPIRATION);
    }
}
