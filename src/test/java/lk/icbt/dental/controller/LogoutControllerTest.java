package lk.icbt.dental.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Tests HTTP logout controller operations.
 */
class LogoutControllerTest {

    private static final String CONTEXT_PATH =
            "/sunrise-dental-clinic-system";

    private HttpServletRequest request;

    private HttpServletResponse response;

    private HttpSession session;

    private LogoutController logoutController;

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

        session =
                mock(HttpSession.class);

        logoutController =
                new LogoutController();

        when(request.getContextPath())
                .thenReturn(CONTEXT_PATH);
    }

    /**
     * Logout must invalidate an existing session.
     */
    @Test
    @DisplayName(
            "Should invalidate session during logout")
    void shouldInvalidateSessionDuringLogout()
            throws Exception {

        when(request.getSession(false))
                .thenReturn(session);

        logoutController.doPost(
                request,
                response);

        verify(request)
                .getSession(false);

        verify(session)
                .invalidate();

        verify(response)
                .sendRedirect(
                        CONTEXT_PATH
                        + "/login?logout=success");
    }

    /**
     * Logout must remain safe when no
     * session currently exists.
     */
    @Test
    @DisplayName(
            "Should safely logout without a session")
    void shouldSafelyLogoutWithoutSession()
            throws Exception {

        when(request.getSession(false))
                .thenReturn(null);

        logoutController.doPost(
                request,
                response);

        verify(session, never())
                .invalidate();

        verify(response)
                .sendRedirect(
                        CONTEXT_PATH
                        + "/login?logout=success");
    }

    /**
     * A GET logout request must perform
     * the same logout operation.
     */
    @Test
    @DisplayName(
            "Should support logout using GET")
    void shouldSupportLogoutUsingGet()
            throws Exception {

        when(request.getSession(false))
                .thenReturn(session);

        logoutController.doGet(
                request,
                response);

        verify(session)
                .invalidate();

        verify(response)
                .sendRedirect(
                        CONTEXT_PATH
                        + "/login?logout=success");
    }
}