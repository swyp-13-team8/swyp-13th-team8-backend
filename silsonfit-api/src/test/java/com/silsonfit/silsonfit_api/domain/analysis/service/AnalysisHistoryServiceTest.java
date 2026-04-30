package com.silsonfit.silsonfit_api.domain.analysis.service;

import com.silsonfit.silsonfit_api.domain.analysis.dto.AnalysisHistoryCreateCommand;
import com.silsonfit.silsonfit_api.domain.analysis.dto.AnalysisHistoryListResponse;
import com.silsonfit.silsonfit_api.domain.analysis.entity.AnalysisHistory;
import com.silsonfit.silsonfit_api.domain.analysis.repository.AnalysisHistoryRepository;
import com.silsonfit.silsonfit_api.global.error.BusinessException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
    void 분석이력_삭제_테스트() {
        analysisHistoryService.deleteHistory(1L, dummy2.getId());

        assertThat(analysisHistoryRepository.findById(dummy2.getId()).isPresent())
                .isFalse();
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

}