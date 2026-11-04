package com.mss301.account.service;

import com.mss301.account.model.StoreAccount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class SessionStore {

    private final ConcurrentMap<String, Session> sessions = new ConcurrentHashMap<>();
    private final Duration timeToLive;

    public SessionStore(@Value("${app.session.ttl:PT8H}") Duration timeToLive) {
        this.timeToLive = timeToLive;
    }

    public IssuedSession issue(StoreAccount account) {
        String token = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plus(timeToLive);
        sessions.put(token, new Session(account.getAccountId(), expiresAt));
        return new IssuedSession(token, expiresAt);
    }

    public Optional<Session> resolve(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        Session session = sessions.get(token);
        if (session == null) {
            return Optional.empty();
        }
        if (session.expiresAt().isBefore(Instant.now())) {
            sessions.remove(token);
            return Optional.empty();
        }
        return Optional.of(session);
    }

    public void revoke(String token) {
        if (token != null) {
            sessions.remove(token);
        }
    }

    public record IssuedSession(String token, Instant expiresAt) {
    }

    public record Session(Integer accountId, Instant expiresAt) {
    }
}

