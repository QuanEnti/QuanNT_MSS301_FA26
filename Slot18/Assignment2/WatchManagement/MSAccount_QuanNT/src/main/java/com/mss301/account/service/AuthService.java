package com.mss301.account.service;

import com.mss301.account.dto.LoginRequest;
import com.mss301.account.dto.LoginResponse;
import com.mss301.account.dto.SessionValidationResponse;
import com.mss301.account.exception.ApiException;
import com.mss301.account.model.StoreAccount;
import com.mss301.account.repository.StoreAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final StoreAccountRepository accountRepository;
    private final SessionStore sessionStore;

    public AuthService(StoreAccountRepository accountRepository, SessionStore sessionStore) {
        this.accountRepository = accountRepository;
        this.sessionStore = sessionStore;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        StoreAccount account = accountRepository.findByUsernameIgnoreCase(request.username().trim())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));

        if (!account.getPassword().equals(request.password())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
        if (!account.isActive()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Account is inactive");
        }

        SessionStore.IssuedSession session = sessionStore.issue(account);
        return new LoginResponse(
                session.token(),
                "Bearer",
                session.expiresAt(),
                account.getAccountId(),
                account.getUsername(),
                account.getEmail(),
                account.getRole()
        );
    }

    @Transactional(readOnly = true)
    public SessionValidationResponse validate(String token) {
        return sessionStore.resolve(token)
                .flatMap(session -> accountRepository.findById(session.accountId()))
                .filter(StoreAccount::isActive)
                .map(account -> new SessionValidationResponse(
                        true,
                        account.getAccountId(),
                        account.getUsername(),
                        account.getRole()
                ))
                .orElseGet(SessionValidationResponse::invalid);
    }

    public void logout(String token) {
        sessionStore.revoke(token);
    }
}

