package com.mss301.watch.service;

import com.mss301.watch.dto.SagaLogResponse;
import com.mss301.watch.dto.SagaResponse;
import com.mss301.watch.exception.ApiException;
import com.mss301.watch.repository.WatchSagaLogRepository;
import com.mss301.watch.repository.WatchSagaRepository;
import com.mss301.watch.saga.SagaOperationType;
import com.mss301.watch.saga.SagaStatus;
import com.mss301.watch.saga.WatchSaga;
import com.mss301.watch.saga.WatchSagaLog;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SagaService {

    private final WatchSagaRepository sagaRepository;
    private final WatchSagaLogRepository logRepository;

    public SagaService(WatchSagaRepository sagaRepository, WatchSagaLogRepository logRepository) {
        this.sagaRepository = sagaRepository;
        this.logRepository = logRepository;
    }

    @Transactional
    public String start(SagaOperationType type, Integer watchId) {
        WatchSaga saga = sagaRepository.save(new WatchSaga(type, watchId));
        saga.changeStatus(SagaStatus.EXECUTING);
        logRepository.save(new WatchSagaLog(saga, "START", "SUCCESS", type + " Saga started"));
        return saga.getOperationId();
    }

    @Transactional
    public void attachWatch(String operationId, Integer watchId) {
        requireSaga(operationId).attachWatch(watchId);
    }

    @Transactional
    public void log(String operationId, String step, String result, String message) {
        logRepository.save(new WatchSagaLog(requireSaga(operationId), step, result, message));
    }

    @Transactional
    public void fail(String operationId, String reason) {
        WatchSaga saga = requireSaga(operationId);
        saga.fail(truncate(reason));
        logRepository.save(new WatchSagaLog(saga, "SAGA", "FAILED", truncate(reason)));
    }

    @Transactional
    public void markCompensating(String operationId) {
        requireSaga(operationId).changeStatus(SagaStatus.COMPENSATING);
    }

    @Transactional
    public void markCompensated(String operationId) {
        WatchSaga saga = requireSaga(operationId);
        saga.changeStatus(SagaStatus.COMPENSATED);
        logRepository.save(new WatchSagaLog(saga, "SAGA", "COMPENSATED", "Compensation completed"));
    }

    @Transactional
    public void complete(String operationId) {
        WatchSaga saga = requireSaga(operationId);
        saga.changeStatus(SagaStatus.COMPLETED);
        logRepository.save(new WatchSagaLog(saga, "SAGA", "COMPLETED", "Saga completed"));
    }

    @Transactional(readOnly = true)
    public List<SagaResponse> findAll() {
        return sagaRepository.findAllByOrderByStartedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SagaResponse findByOperationId(String operationId) {
        return toResponse(requireSaga(operationId));
    }

    private WatchSaga requireSaga(String operationId) {
        return sagaRepository.findByOperationId(operationId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Saga was not found"));
    }

    private SagaResponse toResponse(WatchSaga saga) {
        List<SagaLogResponse> logs = logRepository.findBySagaSagaIdOrderByLogIdAsc(saga.getSagaId())
                .stream()
                .map(log -> new SagaLogResponse(
                        log.getStep(),
                        log.getResult(),
                        log.getMessage(),
                        log.getLoggedAt()
                ))
                .toList();
        return new SagaResponse(
                saga.getOperationId(),
                saga.getOperationType(),
                saga.getWatchId(),
                saga.getStatus(),
                saga.getFailureReason(),
                saga.getStartedAt(),
                saga.getCompletedAt(),
                logs
        );
    }

    private String truncate(String message) {
        String safeMessage = message == null ? "Unknown error" : message;
        return safeMessage.length() <= 1000 ? safeMessage : safeMessage.substring(0, 1000);
    }
}
