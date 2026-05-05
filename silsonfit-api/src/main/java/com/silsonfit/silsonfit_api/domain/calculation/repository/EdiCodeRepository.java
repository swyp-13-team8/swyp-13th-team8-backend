package com.silsonfit.silsonfit_api.domain.calculation.repository;

import com.silsonfit.silsonfit_api.domain.calculation.entity.EdiCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * EDI 수가 코드 Repository
 */
public interface EdiCodeRepository extends JpaRepository<EdiCode, Long> {

    /**
     * 수가 코드 기반 EDI 코드 조회
     */
    Optional<EdiCode> findByCode(String code);
}
