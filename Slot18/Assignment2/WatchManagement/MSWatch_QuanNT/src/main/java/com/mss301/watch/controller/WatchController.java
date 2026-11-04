package com.mss301.watch.controller;

import com.mss301.watch.dto.WatchRequest;
import com.mss301.watch.dto.WatchResponse;
import com.mss301.watch.security.AdminGuard;
import com.mss301.watch.saga.SagaResult;
import com.mss301.watch.service.WatchSagaOrchestrator;
import com.mss301.watch.service.WatchService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/watches")
public class WatchController {

    private final WatchService watchService;
    private final WatchSagaOrchestrator sagaOrchestrator;
    private final AdminGuard adminGuard;

    public WatchController(
            WatchService watchService,
            WatchSagaOrchestrator sagaOrchestrator,
            AdminGuard adminGuard
    ) {
        this.watchService = watchService;
        this.sagaOrchestrator = sagaOrchestrator;
        this.adminGuard = adminGuard;
    }

    @GetMapping
    public List<WatchResponse> findAll() {
        return watchService.findAll();
    }

    @GetMapping("/{watchId}")
    public WatchResponse findById(@PathVariable Integer watchId) {
        return watchService.findById(watchId);
    }

    @PostMapping
    public ResponseEntity<WatchResponse> create(
            @RequestHeader(name = "X-Account-Role", required = false) Integer role,
            @Valid @RequestBody WatchRequest request
    ) {
        adminGuard.requireAdministrator(role);
        SagaResult<WatchResponse> result = sagaOrchestrator.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("X-Saga-Operation-Id", result.operationId())
                .header("X-Saga-Status", result.status().name())
                .body(result.value());
    }

    @PutMapping("/{watchId}")
    public ResponseEntity<WatchResponse> update(
            @RequestHeader(name = "X-Account-Role", required = false) Integer role,
            @PathVariable Integer watchId,
            @Valid @RequestBody WatchRequest request
    ) {
        adminGuard.requireAdministrator(role);
        SagaResult<WatchResponse> result = sagaOrchestrator.update(watchId, request);
        return ResponseEntity.ok()
                .header("X-Saga-Operation-Id", result.operationId())
                .header("X-Saga-Status", result.status().name())
                .body(result.value());
    }

    @DeleteMapping("/{watchId}")
    public ResponseEntity<Void> delete(
            @RequestHeader(name = "X-Account-Role", required = false) Integer role,
            @PathVariable Integer watchId
    ) {
        adminGuard.requireAdministrator(role);
        SagaResult<Void> result = sagaOrchestrator.delete(watchId);
        return ResponseEntity.noContent()
                .header("X-Saga-Operation-Id", result.operationId())
                .header("X-Saga-Status", result.status().name())
                .build();
    }
}

