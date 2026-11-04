package com.mss301.watch.security;

import com.mss301.watch.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class AdminGuard {

    public void requireAdministrator(Integer role) {
        if (role == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Authentication is required");
        }
        if (role != 1) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Administrator role is required");
        }
    }
}

