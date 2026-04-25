package com.silsonfit.silsonfit_api.domain.auth.client;

import com.silsonfit.silsonfit_api.global.error.BusinessException;
import com.silsonfit.silsonfit_api.global.error.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * KakaoClient 통합 테스트
 *
 * MockRestServiceServer로 카카오 API 응답을 스텁한 뒤,
 * RestClient 호출 → JSON 파싱 → KakaoUserInfo 반환 흐름을 검증한다.
 */
class KakaoClientTest {

    private static final String KAKAO_USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";

    private MockRestServiceServer server;
    private KakaoClient kakaoClient;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        this.server = MockRestServiceServer.bindTo(builder).build();
        this.kakaoClient = new KakaoClient(KAKAO_USER_INFO_URI, builder);
    }

    @Test
    @DisplayName("정상 응답 시 id/name/email/profileImageUrl 모두 매핑된다")
    void getUserInfo_success() {
        server.expect(requestTo(KAKAO_USER_INFO_URI))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer test-token"))
                .andRespond(withSuccess("""
                        {
                          "id": 12345,
                          "kakao_account": {
                            "email": "test@example.com",
                            "profile": {
                              "nickname": "홍길동",
                              "profile_image_url": "http://example.com/img.jpg"
                            }
                          }
                        }
                        """, MediaType.APPLICATION_JSON));

        KakaoClient.KakaoUserInfo info = kakaoClient.getUserInfo("test-token");

        assertThat(info.id()).isEqualTo(12345L);
        assertThat(info.name()).isEqualTo("홍길동");
        assertThat(info.email()).isEqualTo("test@example.com");
        assertThat(info.profileImageUrl()).isEqualTo("http://example.com/img.jpg");
    }

    @Test
    @DisplayName("선택 동의 항목(email, profile_image_url) 미포함 응답은 null로 처리된다")
    void getUserInfo_optional_fields_null() {
        server.expect(requestTo(KAKAO_USER_INFO_URI))
                .andRespond(withSuccess("""
                        {
                          "id": 12345,
                          "kakao_account": {
                            "profile": {
                              "nickname": "홍길동"
                            }
                          }
                        }
                        """, MediaType.APPLICATION_JSON));

        KakaoClient.KakaoUserInfo info = kakaoClient.getUserInfo("test-token");

        assertThat(info.id()).isEqualTo(12345L);
        assertThat(info.name()).isEqualTo("홍길동");
        assertThat(info.email()).isNull();
        assertThat(info.profileImageUrl()).isNull();
    }

    @Test
    @DisplayName("401 응답은 INVALID_KAKAO_TOKEN으로 변환된다")
    void getUserInfo_401_invalidKakaoToken() {
        server.expect(requestTo(KAKAO_USER_INFO_URI))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        assertThatThrownBy(() -> kakaoClient.getUserInfo("invalid"))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_KAKAO_TOKEN);
    }

    @Test
    @DisplayName("5xx 응답은 KAKAO_SERVER_ERROR로 변환된다")
    void getUserInfo_5xx_kakaoServerError() {
        server.expect(requestTo(KAKAO_USER_INFO_URI))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> kakaoClient.getUserInfo("token"))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.KAKAO_SERVER_ERROR);
    }
}
