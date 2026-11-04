package com.mss301.watch.controller;

import com.mss301.watch.dto.CategoryResponse;
import com.mss301.watch.service.WatchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final WatchService watchService;

    public CategoryController(WatchService watchService) {
        this.watchService = watchService;
    }

    @GetMapping
    public List<CategoryResponse> findAll() {
        return watchService.findAllCategories();
    }
}

