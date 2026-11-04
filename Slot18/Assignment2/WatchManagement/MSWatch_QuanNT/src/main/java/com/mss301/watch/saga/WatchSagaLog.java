package com.mss301.watch.saga;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "WatchSagaLogs")
public class WatchSagaLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LogID")
    private Long logId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SagaID", nullable = false)
    private WatchSaga saga;

    @Column(name = "Step", nullable = false, length = 50)
    private String step;

    @Column(name = "Result", nullable = false, length = 20)
    private String result;

    @Column(name = "Message", nullable = false, length = 1000)
    private String message;

    @Column(name = "LoggedAt", nullable = false)
    private Instant loggedAt;

    protected WatchSagaLog() {
    }

    public WatchSagaLog(WatchSaga saga, String step, String result, String message) {
        this.saga = saga;
        this.step = step;
        this.result = result;
        this.message = message;
        this.loggedAt = Instant.now();
    }

    public String getStep() {
        return step;
    }

    public String getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public Instant getLoggedAt() {
        return loggedAt;
    }
}
