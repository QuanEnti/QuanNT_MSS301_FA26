package com.mss301.account.service;

import com.mss301.account.dto.LoginRequest;
import com.mss301.account.dto.LoginResponse;
import com.mss301.account.exception.ApiException;
import com.mss301.account.model.StoreAccount;
import com.mss301.account.repository.StoreAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private StoreAccountRepository accountRepository;

    @Mock
    private SessionStore sessionStore;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginReturnsBearerSessionForActiveAccount() {
        StoreAccount account = new StoreAccount("adminsys", "admin@drawingpen.com.vn", "@1", 1, true);
        Instant expiresAt = Instant.parse("2026-11-05T00:00:00Z");
        when(accountRepository.findByUsernameIgnoreCase("adminsys")).thenReturn(Optional.of(account));
        when(sessionStore.issue(account)).thenReturn(new SessionStore.IssuedSession("token", expiresAt));

        LoginResponse response = authService.login(new LoginRequest("adminsys", "@1"));

        assertThat(response.accessToken()).isEqualTo("token");
        assertThat(response.role()).isEqualTo(1);
        verify(sessionStore).issue(account);
    }

    @Test
    void loginRejectsInvalidPassword() {
        StoreAccount account = new StoreAccount("adminsys", "admin@drawingpen.com.vn", "@1", 1, true);
        when(accountRepository.findByUsernameIgnoreCase("adminsys")).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> authService.login(new LoginRequest("adminsys", "wrong")))
                .isInstanceOf(ApiException.class)
                .extracting(exception -> ((ApiException) exception).getStatus())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void loginRejectsInactiveAccount() {
        StoreAccount account = new StoreAccount("member02", "member02@drawingpen.com.vn", "@1", 3, false);
        when(accountRepository.findByUsernameIgnoreCase("member02")).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> authService.login(new LoginRequest("member02", "@1")))
                .isInstanceOf(ApiException.class)
                .hasMessage("Account is inactive");
    }
}

