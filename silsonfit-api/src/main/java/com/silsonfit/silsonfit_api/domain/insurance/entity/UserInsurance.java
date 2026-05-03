package com.silsonfit.silsonfit_api.domain.insurance.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 보험 등록 엔티티
 *
 * 사용자가 등록한 보험 정보를 관리한다. (최대 5개)
 */
@Entity
@Table(name = "user_insurances")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInsurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자 ID (User 도메인 → ID 참조)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 보험 상품 (Insurance 도메인 → 객체 참조)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insurance_id", nullable = false)
    private Insurance insurance;

    // 가입 연월 (예: "2020-03")
    @Column(nullable = false)
    private String subscribedAt;

    // 비급여 특약 가입 여부 (4세대)
    @Column(nullable = false)
    private boolean hasNonCoveredRider;

    @Builder
    public UserInsurance(Long userId, Insurance insurance, String subscribedAt, boolean hasNonCoveredRider) {
        this.userId = userId;
        this.insurance = insurance;
        this.subscribedAt = subscribedAt;
        this.hasNonCoveredRider = hasNonCoveredRider;
    }

    /**
     * 소유권 확인
     *
     * @param userId 확인할 사용자 ID
     * @return 본인 보험 여부
     */
    public boolean isOwnedBy(Long userId) {
        return this.userId.equals(userId);
    }
}
