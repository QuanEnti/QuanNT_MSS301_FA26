package com.mss301.gateway.filter;

import com.mss301.gateway.client.AccountSessionClient;
import com.mss301.gateway.client.SessionValidationResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Component
public class AdminAuthorizationFilter extends OncePerRequestFilter {

    private static final Set<String> MUTATING_METHODS = Set.of(
            HttpMethod.POST.name(),
            HttpMethod.PUT.name(),
            HttpMethod.DELETE.name()
    );

    private final AccountSessionClient accountSessionClient;

    public AdminAuthorizationFilter(AccountSessionClient accountSessionClient) {
        this.accountSessionClient = accountSessionClient;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/watches")
                || !MUTATING_METHODS.contains(request.getMethod());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = bearerToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        if (token == null) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "A Bearer token is required");
            return;
        }

        SessionValidationResponse session;
        try {
            session = accountSessionClient.validate(token);
        } catch (RestClientException exception) {
            writeError(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Account Service is unavailable");
            return;
        }

        if (!session.valid()) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "The session is invalid or expired");
            return;
        }
        if (session.role() == null || session.role() != 1) {
            writeError(response, HttpServletResponse.SC_FORBIDDEN, "Administrator role is required");
            return;
        }

        HttpServletRequest authenticatedRequest = new AccountHeaderRequest(
                request,
                Map.of(
                        "X-Account-Id", session.accountId().toString(),
                        "X-Account-Username", session.username(),
                        "X-Account-Role", session.role().toString()
                )
        );
        filterChain.doFilter(authenticatedRequest, response);
    }

    private String bearerToken(String authorization) {
        if (authorization == null || !authorization.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return null;
        }
        String token = authorization.substring(7).trim();
        return token.isEmpty() ? null : token;
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"status\":" + status + ",\"message\":\"" + message + "\"}");
    }

    private static final class AccountHeaderRequest extends HttpServletRequestWrapper {

        private final Map<String, String> accountHeaders;

        private AccountHeaderRequest(HttpServletRequest request, Map<String, String> accountHeaders) {
            super(request);
            this.accountHeaders = accountHeaders;
        }

        @Override
        public String getHeader(String name) {
            return accountHeaders.entrySet().stream()
                    .filter(entry -> entry.getKey().equalsIgnoreCase(name))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElseGet(() -> isAccountHeader(name) ? null : super.getHeader(name));
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            String value = getHeader(name);
            return value == null
                    ? Collections.emptyEnumeration()
                    : Collections.enumeration(java.util.List.of(value));
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            Set<String> names = new LinkedHashSet<>();
            Enumeration<String> originalNames = super.getHeaderNames();
            while (originalNames != null && originalNames.hasMoreElements()) {
                String name = originalNames.nextElement();
                if (!isAccountHeader(name)) {
                    names.add(name);
                }
            }
            names.addAll(accountHeaders.keySet());
            return Collections.enumeration(new ArrayList<>(names));
        }

        private boolean isAccountHeader(String name) {
            return name != null && name.toLowerCase(Locale.ROOT).startsWith("x-account-");
        }
    }
}
