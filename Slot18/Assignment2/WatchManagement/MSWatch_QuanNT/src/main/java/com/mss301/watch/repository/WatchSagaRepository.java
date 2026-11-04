package com.mss301.watch.repository;

import com.mss301.watch.saga.WatchSaga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WatchSagaRepository extends JpaRepository<WatchSaga, Long> {

    Optional<WatchSaga> findByOperationId(String operationId);

    List<WatchSaga> findAllByOrderByStartedAtDesc();
}
