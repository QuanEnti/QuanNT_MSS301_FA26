package com.mss301.gateway.filter;

import com.mss301.gateway.client.AccountSessionClient;
import com.mss301.gateway.client.SessionValidationResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminAuthorizationFilterTest {

    @Mock
    private AccountSessionClient accountSessionClient;

    @Test
    void rejectsMutationWithoutToken() throws Exception {
        AdminAuthorizationFilter filter = new AdminAuthorizationFilter(accountSessionClient);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/watches");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (req, res) -> {
            throw new AssertionError("Filter chain must not be called");
        });

        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    void rejectsAuthenticatedStaff() throws Exception {
        AdminAuthorizationFilter filter = new AdminAuthorizationFilter(accountSessionClient);
        MockHttpServletRequest request = requestWithToken("staff-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(accountSessionClient.validate("staff-token"))
                .thenReturn(new SessionValidationResponse(true, 2, "staff02", 2));

        filter.doFilter(request, response, (req, res) -> {
            throw new AssertionError("Filter chain must not be called");
        });

        assertThat(response.getStatus()).isEqualTo(403);
    }

    @Test
    void forwardsTrustedAdministratorHeaders() throws Exception {
        AdminAuthorizationFilter filter = new AdminAuthorizationFilter(accountSessionClient);
        MockHttpServletRequest request = requestWithToken("admin-token");
        request.addHeader("X-Account-Role", "3");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<HttpServletRequest> forwarded = new AtomicReference<>();
        when(accountSessionClient.validate("admin-token"))
                .thenReturn(new SessionValidationResponse(true, 1, "adminsys", 1));

        filter.doFilter(request, response, (req, res) -> forwarded.set((HttpServletRequest) req));

        assertThat(forwarded.get().getHeader("X-Account-Id")).isEqualTo("1");
        assertThat(forwarded.get().getHeader("X-Account-Username")).isEqualTo("adminsys");
        assertThat(forwarded.get().getHeader("X-Account-Role")).isEqualTo("1");
    }

    private MockHttpServletRequest requestWithToken(String token) {
        MockHttpServletRequest request = new MockHttpServletRequest("DELETE", "/api/watches/10");
        request.addHeader("Authorization", "Bearer " + token);
        return request;
    }
}
