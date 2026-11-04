package com.mss301.watch.controller;

import com.mss301.watch.dto.SagaResponse;
import com.mss301.watch.service.SagaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sagas")
public class SagaController {

    private final SagaService sagaService;

    public SagaController(SagaService sagaService) {
        this.sagaService = sagaService;
    }

    @GetMapping
    public List<SagaResponse> findAll() {
        return sagaService.findAll();
    }

    @GetMapping("/{operationId}")
    public SagaResponse findByOperationId(@PathVariable String operationId) {
        return sagaService.findByOperationId(operationId);
    }
}
