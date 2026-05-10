package com.silsonfit.silsonfit_api.domain.analysis.service;

import com.silsonfit.silsonfit_api.domain.analysis.dto.AnalysisHistoryCreateCommand;
import com.silsonfit.silsonfit_api.domain.analysis.dto.AnalysisHistoryListResponse;
import com.silsonfit.silsonfit_api.domain.analysis.entity.AnalysisHistory;
import com.silsonfit.silsonfit_api.domain.analysis.repository.AnalysisHistoryRepository;
import com.silsonfit.silsonfit_api.global.error.BusinessException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class AnalysisHistoryServiceTest {

    @Autowired
    AnalysisHistoryService analysisHistoryService;
    @Autowired
    AnalysisHistoryRepository analysisHistoryRepository;
    @Autowired
    EntityManager em; // 네이티브 쿼리로 DB 상태 확인을 위해 추가

    AnalysisHistory dummy1;
    AnalysisHistory dummy2;

    @BeforeEach
    void setUp() {

        dummy1 = AnalysisHistory.create(new AnalysisHistoryCreateCommand(1L, "test1.pdf", "test1", "testCompany1",
                "testProduct1", "testType1", "1", "testCoverage1", "testPoint1", null));
        dummy2 = AnalysisHistory.create(new AnalysisHistoryCreateCommand(1L, "test2.pdf", "test2", "testCompany2",
                "testProduct2", "testType2", "2", "testCoverage2", "testPoint2", null));
        analysisHistoryRepository.saveAll(List.of(dummy1, dummy2));
    }

    @Test
    void 리스트_조회시_즐겨찾기_우선조회() {
        dummy2.toggleFavorite();

        List<AnalysisHistoryListResponse> histories =
                analysisHistoryService.getHistories(1L, PageRequest.of(0, 5)).getContent();

        // dummy1이 먼저 저장되었지만 즐겨찾기 우선이므로 dummy2가 리스트의 0번째가 된다.
        assertThat(histories.get(0).companyName()).isEqualTo("testCompany2");
    }

    @Test
    void 다른유저가_다른이력을_조회하려고하면_예외발생() {
        assertThatThrownBy(() -> {
            analysisHistoryService.getHistoryDetail(2L, dummy1.getId());
        }).isInstanceOf(BusinessException.class)
                .hasMessageContaining("해당 분석 이력에 대한 접근 권한이 없습니다.");
    }

    @Test
    void 다른유저가_다른이력을_삭제하려고하면_예외발생() {
        assertThatThrownBy(() -> {
            analysisHistoryService.deleteHistory(2L,dummy1.getId());
        }).isInstanceOf(BusinessException.class)
                .hasMessageContaining("해당 분석 이력에 대한 접근 권한이 없습니다.");
    }

    @Test
    void 분석이력_소프트삭제_테스트() {
        Long userId = 1L;
        Long historyId = dummy2.getId();

        analysisHistoryService.deleteHistory(userId, historyId);

        // 영속성 컨텍스트를 비워야 다음 조회 시 DB에 쿼리를 날린다.
        em.flush();
        em.clear();

        // DB 에는 있지만 @SQLRestriction 애노테이션 때문에 조회가 안되어야한다.
        assertThat(analysisHistoryRepository.findById(historyId))
                .isEmpty();

        // DB 에는 있기 때문에 직접 쿼리문을 날려 is_deleted 가 true 인지 확인
        Boolean isDeleted = (Boolean) em.createNativeQuery(
                        "select is_deleted from analysis_history where analysis_history_id = :id")
                .setParameter("id", historyId)
                .getSingleResult();

        assertThat(isDeleted).isTrue();
    }

    @Test
    void 즐겨찾기_리스트_조회_테스트() {
        Long userId = 1L;

        dummy2.toggleFavorite();

        PageRequest pageRequest = PageRequest.of(0, 5);

        Page<AnalysisHistory> favoriteHistories =
                analysisHistoryRepository.findFavoriteHistories(userId, pageRequest);

        List<AnalysisHistory> content = favoriteHistories.getContent();

        assertThat(content.size()).isEqualTo(1);
        assertThat(content.get(0).getId()).isEqualTo(dummy2.getId());
        assertThat(content.get(0).getIsFavorite()).isTrue();
    }

}