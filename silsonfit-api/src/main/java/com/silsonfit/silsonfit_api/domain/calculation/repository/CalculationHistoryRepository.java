package com.silsonfit.silsonfit_api.domain.calculation.repository;

import com.silsonfit.silsonfit_api.domain.calculation.entity.CalculationHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 실손 보험 계산 이력 Repository
 */
public interface CalculationHistoryRepository extends JpaRepository<CalculationHistory, Long> {

    /**
     * 사용자 계산 이력 즐겨찾기 목록 조회
     */
    @Query("""
            select history
            from CalculationHistory history
            where history.userId = :userId
              and history.isFavorite = true
            order by history.createdAt desc, history.id desc
            """)
    List<CalculationHistory> findFavoritesByUserId(@Param("userId") Long userId);
}
