package com.silsonfit.silsonfit_api.global.common;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

/**
 * 생성 일자 및 수정 일자 모두 가지는 엔티티의 공통 시간 필드를 처리하는 클래스
 */
@Getter
@MappedSuperclass
public abstract class BaseTimeEntity extends BaseCreatedTimeEntity{

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
