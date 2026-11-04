package com.mss301.watch.repository;

import com.mss301.watch.saga.WatchSagaLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WatchSagaLogRepository extends JpaRepository<WatchSagaLog, Long> {

    List<WatchSagaLog> findBySagaSagaIdOrderByLogIdAsc(Long sagaId);
}
