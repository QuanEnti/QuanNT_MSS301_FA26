package com.mss301.watch.repository;

import com.mss301.watch.model.Watch;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WatchRepository extends JpaRepository<Watch, Integer> {

    @EntityGraph(attributePaths = "category")
    List<Watch> findAllByOrderByWatchIdDesc();

    @Override
    @EntityGraph(attributePaths = "category")
    Optional<Watch> findById(Integer watchId);
}

