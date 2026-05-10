package com.silsonfit.silsonfit_api.domain.auth.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.silsonfit.silsonfit_api.global.error.BusinessException;
import com.silsonfit.silsonfit_api.global.error.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 카카오 API 통신 Client
 *
 * 카카오 Access Token으로 사용자 정보 조회
 * 토큰 무효 시 INVALID_KAKAO_TOKEN, 그 외 오류는 KAKAO_SERVER_ERROR로 변환
 */
@Component
public class KakaoClient {

    private final String kakaoUserInfoUri;
    private final RestClient restClient;

    public KakaoClient(
            @Value("${kakao.user-info-uri}") String kakaoUserInfoUri,
            RestClient.Builder restClientBuilder
    ) {
        this.kakaoUserInfoUri = kakaoUserInfoUri;
        this.restClient = restClientBuilder.build();
    }

    /**
     * 카카오 Access Token으로 사용자 정보 조회
     */
    public KakaoUserInfo getUserInfo(String kakaoAccessToken) {
        KakaoResponse response;
        try {
            response = restClient.get()
                    .uri(kakaoUserInfoUri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + kakaoAccessToken)
                    .retrieve()
                    .onStatus(status -> status.value() == 401, (req, res) -> {
                        throw new BusinessException(ErrorCode.INVALID_KAKAO_TOKEN);
                    })
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        throw new BusinessException(ErrorCode.KAKAO_SERVER_ERROR);
                    })
                    .body(KakaoResponse.class);
        } catch (BusinessException e) {
            // onStatus에서 던진 예외는 그대로 전파
            throw e;
        } catch (Exception e) {
            // 네트워크 오류, 응답 파싱 실패 등
            throw new BusinessException(ErrorCode.KAKAO_SERVER_ERROR);
        }

        if (response == null) {
            throw new BusinessException(ErrorCode.KAKAO_SERVER_ERROR);
        }

        // 카카오 선택 동의 항목은 null일 수 있으므로 null 체크 후 추출
        KakaoAccount account = response.kakaoAccount();
        Profile profile = (account != null) ? account.profile() : null;

        return new KakaoUserInfo(
                response.id(),
                (profile != null) ? profile.nickname() : null,
                (account != null) ? account.email() : null,
                (profile != null) ? profile.profileImageUrl() : null
        );
    }

    /**
     * KakaoClient 외부 반환 사용자 정보
     *
     * name 필드는 카카오 profile.nickname 매핑
     * User 엔티티의 name과 통일
     */
    public record KakaoUserInfo(
            Long id,
            String name,
            String email,
            String profileImageUrl
    ) {
    }

    // ── 카카오 API 응답 매핑용 내부 record ──

    private record KakaoResponse(
            Long id,
            @JsonProperty("kakao_account") KakaoAccount kakaoAccount
    ) {
    }

    private record KakaoAccount(
            String email,
            Profile profile
    ) {
    }

    private record Profile(
            String nickname,
            @JsonProperty("profile_image_url") String profileImageUrl
    ) {
    }
}
