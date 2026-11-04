package com.mss301.watch.repository;

import com.mss301.watch.model.WatchCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchCategoryRepository extends JpaRepository<WatchCategory, Integer> {
}

