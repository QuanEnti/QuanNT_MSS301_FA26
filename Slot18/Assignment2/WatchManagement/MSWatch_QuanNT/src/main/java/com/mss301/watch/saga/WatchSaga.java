package com.mss301.watch.saga;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "WatchSagas")
public class WatchSaga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SagaID")
    private Long sagaId;

    @Column(name = "OperationID", nullable = false, unique = true, length = 36)
    private String operationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "OperationType", nullable = false, length = 20)
    private SagaOperationType operationType;

    @Column(name = "WatchID")
    private Integer watchId;

    @Enumerated(EnumType.STRING)
    @Column(name = "SagaStatus", nullable = false, length = 20)
    private SagaStatus status;

    @Column(name = "FailureReason", length = 1000)
    private String failureReason;

    @Column(name = "StartedAt", nullable = false)
    private Instant startedAt;

    @Column(name = "CompletedAt")
    private Instant completedAt;

    protected WatchSaga() {
    }

    public WatchSaga(SagaOperationType operationType, Integer watchId) {
        this.operationId = UUID.randomUUID().toString();
        this.operationType = operationType;
        this.watchId = watchId;
        this.status = SagaStatus.PENDING;
        this.startedAt = Instant.now();
    }

    public void attachWatch(Integer watchId) {
        this.watchId = watchId;
    }

    public void changeStatus(SagaStatus status) {
        this.status = status;
        if (status == SagaStatus.COMPLETED || status == SagaStatus.COMPENSATED) {
            this.completedAt = Instant.now();
        }
    }

    public void fail(String reason) {
        this.status = SagaStatus.FAILED;
        this.failureReason = reason;
    }

    public Long getSagaId() {
        return sagaId;
    }

    public String getOperationId() {
        return operationId;
    }

    public SagaOperationType getOperationType() {
        return operationType;
    }

    public Integer getWatchId() {
        return watchId;
    }

    public SagaStatus getStatus() {
        return status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }
}
