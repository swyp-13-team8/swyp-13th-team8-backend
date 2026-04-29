package com.silsonfit.silsonfit_api.domain.user.service;

import com.silsonfit.silsonfit_api.global.error.BusinessException;
import com.silsonfit.silsonfit_api.global.error.ErrorCode;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 닉네임 금칙어 검증
 *
 * 욕설, 성적 표현, 비하 표현 등을 포함한 닉네임을 차단
 */
@Component
public class NicknameValidator {

    private static final List<String> FORBIDDEN_WORDS = List.of(
            // 욕설
            "시발", "씨발", "시bal", "ㅅㅂ", "ㅆㅂ", "씹", "좆", "지랄", "개새끼",
            "병신", "ㅂㅅ", "미친놈", "미친년", "꺼져", "닥쳐",
            // 성적 표현
            "섹스", "야동", "포르노", "성인", "자위",
            // 비하 표현
            "느금마", "한남", "한녀", "틀딱", "급식충"
    );

    /**
     * 금칙어 포함 여부 검증
     *
     * @param nickname 검증할 닉네임
     */
    public void validate(String nickname) {
        String lower = nickname.toLowerCase();
        for (String word : FORBIDDEN_WORDS) {
            if (lower.contains(word.toLowerCase())) {
                throw new BusinessException(ErrorCode.INVALID_NICKNAME);
            }
        }
    }
}
