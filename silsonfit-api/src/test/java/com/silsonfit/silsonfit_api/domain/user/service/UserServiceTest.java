package com.silsonfit.silsonfit_api.domain.user.service;

import com.silsonfit.silsonfit_api.domain.user.dto.UserResponse;
import com.silsonfit.silsonfit_api.domain.user.dto.UserUpdateRequest;
import com.silsonfit.silsonfit_api.domain.user.entity.User;
import com.silsonfit.silsonfit_api.domain.user.repository.UserRepository;
import com.silsonfit.silsonfit_api.global.error.BusinessException;
import com.silsonfit.silsonfit_api.global.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

/**
 * UserService 단위 테스트
 *
 * 프로필 조회 및 닉네임 수정 비즈니스 로직 검증
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private NicknameValidator nicknameValidator;

    @InjectMocks
    private UserService userService;

    // ──────────── getMyProfile ────────────

    @Test
    @DisplayName("내 프로필 조회 성공")
    void getMyProfile_success() {
        // given
        User user = userWithId(1L, 111L, "홍길동", "hong@test.com");
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        UserResponse response = userService.getMyProfile(1L);

        // then
        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("홍길동");
        assertThat(response.email()).isEqualTo("hong@test.com");
    }

    @Test
    @DisplayName("존재하지 않는 사용자 프로필 조회 시 USER_NOT_FOUND 예외")
    void getMyProfile_userNotFound() {
        // given
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getMyProfile(999L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    // ──────────── updateMyProfile ────────────

    @Test
    @DisplayName("닉네임 수정 성공 시 엔티티의 이름이 변경된다")
    void updateMyProfile_success() {
        // given
        User user = userWithId(1L, 111L, "홍길동", "hong@test.com");
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        userService.updateMyProfile(1L, new UserUpdateRequest("김철수"));

        // then
        assertThat(user.getName()).isEqualTo("김철수");
    }

    @Test
    @DisplayName("금칙어가 포함된 닉네임 수정 시 INVALID_NICKNAME 예외")
    void updateMyProfile_forbiddenWord() {
        // given
        User user = userWithId(1L, 111L, "홍길동", "hong@test.com");
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> userService.updateMyProfile(1L, new UserUpdateRequest("시발놈")))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_NICKNAME);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 닉네임 수정 시 USER_NOT_FOUND 예외")
    void updateMyProfile_userNotFound() {
        // given
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.updateMyProfile(999L, new UserUpdateRequest("김철수")))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    // ──────────── helper ────────────

    private User userWithId(Long id, Long socialId, String name, String email) {
        User user = User.builder()
                .socialId(socialId)
                .name(name)
                .email(email)
                .build();
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }
}
