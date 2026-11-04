package com.mss301.watch;

import com.mss301.watch.repository.WatchCategoryRepository;
import com.mss301.watch.repository.WatchRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class WatchCatalogIntegrationTest {

    @Autowired
    private WatchCategoryRepository categoryRepository;

    @Autowired
    private WatchRepository watchRepository;

    @Test
    void seedsTheCompleteAssignmentCatalog() {
        assertThat(categoryRepository.count()).isEqualTo(5);
        assertThat(watchRepository.count()).isEqualTo(10);
        assertThat(watchRepository.findAllByOrderByWatchIdDesc().getFirst().getName())
                .isEqualTo("IWC Pilot's Watch Mark XVIII");
    }
}

