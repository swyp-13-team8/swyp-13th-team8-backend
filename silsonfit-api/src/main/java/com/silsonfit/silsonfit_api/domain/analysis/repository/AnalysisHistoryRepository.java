package com.silsonfit.silsonfit_api.domain.analysis.repository;

import com.silsonfit.silsonfit_api.domain.analysis.entity.AnalysisHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnalysisHistoryRepository extends JpaRepository<AnalysisHistory, Long> {

    /**
     * 특정 유저의 분석 이력 목록을 조회 (즐겨찾기 우선, 최신순 정렬)
     *
     * @param userId    조회할 유저 ID
     * @param pageable  페이징 정보
     * @return          분석 이력 페이징 결과
     */
    @Query("""
            select a from AnalysisHistory a
            where a.userId = :userId
            order by a.isFavorite desc, a.createdAt desc
            """)
    Page<AnalysisHistory> findHistoriesByUserId(@Param("user_id") Long userId, Pageable pageable);
}
