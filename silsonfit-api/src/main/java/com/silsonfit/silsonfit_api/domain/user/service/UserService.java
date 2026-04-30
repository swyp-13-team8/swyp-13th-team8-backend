package com.silsonfit.silsonfit_api.domain.user.service;

import com.silsonfit.silsonfit_api.domain.user.dto.UserResponse;
import com.silsonfit.silsonfit_api.domain.user.dto.UserUpdateRequest;
import com.silsonfit.silsonfit_api.domain.user.entity.User;
import com.silsonfit.silsonfit_api.domain.user.repository.UserRepository;
import com.silsonfit.silsonfit_api.global.error.BusinessException;
import com.silsonfit.silsonfit_api.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 프로필 관련 비즈니스 로직
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final NicknameValidator nicknameValidator;

    /**
     * 내 프로필 조회
     */
    @Transactional(readOnly = true)
    public UserResponse getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }

    /**
     * 닉네임 수정
     */
    @Transactional
    public void updateMyProfile(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        nicknameValidator.validate(request.name());
        user.updateName(request.name());
    }
}
