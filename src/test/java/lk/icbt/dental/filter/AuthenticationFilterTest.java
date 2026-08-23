package lk.icbt.dental.filter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Tests application authentication filtering.
 */
class AuthenticationFilterTest {

    private static final String CONTEXT_PATH =
            "/sunrise-dental-clinic-system";

    private HttpServletRequest request;

    private HttpServletResponse response;

    private FilterChain filterChain;

    private HttpSession session;

    private AuthenticationFilter
            authenticationFilter;

    /**
     * Creates fresh test doubles
     * before every test.
     */
    @BeforeEach
    void setUp() {

        request =
                mock(HttpServletRequest.class);

        response =
                mock(HttpServletResponse.class);

        filterChain =
                mock(FilterChain.class);

        session =
                mock(HttpSession.class);

        authenticationFilter =
                new AuthenticationFilter();

        when(request.getContextPath())
                .thenReturn(CONTEXT_PATH);
    }

    /**
     * A user without a session must be redirected
     * away from a protected page.
     */
    @Test
    @DisplayName(
            "Should redirect user without a session")
    void shouldRedirectUserWithoutSession()
            throws Exception {

        prepareRequest("/patients");

        when(request.getSession(false))
                .thenReturn(null);

        authenticationFilter.doFilter(
                request,
                response,
                filterChain);

        verify(response)
                .sendRedirect(
                        CONTEXT_PATH
                        + "/login?session=expired");

        verify(filterChain, never())
                .doFilter(
                        request,
                        response);
    }

    /**
     * A session without an authenticated user
     * must also be redirected.
     */
    @Test
    @DisplayName(
            "Should redirect unauthenticated session")
    void shouldRedirectUnauthenticatedSession()
            throws Exception {

        prepareRequest("/appointments");

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute(
                "authenticatedUser"))
                .thenReturn(null);

        authenticationFilter.doFilter(
                request,
                response,
                filterChain);

        verify(response)
                .sendRedirect(
                        CONTEXT_PATH
                        + "/login?session=expired");

        verify(filterChain, never())
                .doFilter(
                        request,
                        response);
    }

    /**
     * An authenticated user must be permitted
     * to access protected application pages.
     */
    @Test
    @DisplayName(
            "Should allow authenticated user")
    void shouldAllowAuthenticatedUser()
            throws Exception {

        Object authenticatedUser =
                new Object();

        prepareRequest("/bills");

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute(
                "authenticatedUser"))
                .thenReturn(
                        authenticatedUser);

        authenticationFilter.doFilter(
                request,
                response,
                filterChain);

        verify(filterChain)
                .doFilter(
                        request,
                        response);

        verify(response)
                .setHeader(
                        "Cache-Control",
                        "no-cache, no-store, "
                        + "must-revalidate");

        verify(response)
                .setHeader(
                        "Pragma",
                        "no-cache");

        verify(response)
                .setDateHeader(
                        "Expires",
                        0);
    }

    /**
     * The login page must remain publicly
     * accessible.
     */
    @Test
    @DisplayName(
            "Should allow public login page")
    void shouldAllowPublicLoginPage()
            throws Exception {

        prepareRequest("/login");

        authenticationFilter.doFilter(
                request,
                response,
                filterChain);

        verify(filterChain)
                .doFilter(
                        request,
                        response);

        verify(request, never())
                .getSession(false);
    }

    /**
     * CSS and other static assets must remain
     * publicly accessible for the login page.
     */
    @Test
    @DisplayName(
            "Should allow public static assets")
    void shouldAllowPublicStaticAssets()
            throws Exception {

        prepareRequest(
                "/assets/css/style.css");

        authenticationFilter.doFilter(
                request,
                response,
                filterChain);

        verify(filterChain)
                .doFilter(
                        request,
                        response);

        verify(request, never())
                .getSession(false);
    }

    /**
     * Configures the request URI.
     */
    private void prepareRequest(
            String applicationPath) {

        when(request.getRequestURI())
                .thenReturn(
                        CONTEXT_PATH
                        + applicationPath);
    }
}