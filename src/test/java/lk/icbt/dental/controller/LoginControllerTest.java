package lk.icbt.dental.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import lk.icbt.dental.exception.AuthenticationException;
import lk.icbt.dental.model.User;
import lk.icbt.dental.service.AuthenticationService;

/**
 * Tests HTTP login controller operations.
 */
class LoginControllerTest {

    private static final String CONTEXT_PATH =
            "/sunrise-dental-clinic-system";

    private static final String LOGIN_PAGE =
            "/WEB-INF/views/auth/login.jsp";

    private AuthenticationService
            authenticationService;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private HttpSession session;

    private RequestDispatcher dispatcher;

    private User user;

    private LoginController loginController;

    /**
     * Creates fresh test doubles
     * before every test.
     */
    @BeforeEach
    void setUp() {

        authenticationService =
                mock(AuthenticationService.class);

        request =
                mock(HttpServletRequest.class);

        response =
                mock(HttpServletResponse.class);

        session =
                mock(HttpSession.class);

        dispatcher =
                mock(RequestDispatcher.class);

        user =
                mock(User.class);

        loginController =
                new LoginController(
                        authenticationService);
    }

    /**
     * GET /login must display the login page
     * when no authenticated session exists.
     */
    @Test
    @DisplayName(
            "Should display the login page")
    void shouldDisplayLoginPage()
            throws Exception {

        when(request.getSession(false))
                .thenReturn(null);

        when(request.getRequestDispatcher(
                LOGIN_PAGE))
                .thenReturn(dispatcher);

        loginController.doGet(
                request,
                response);

        verify(request)
                .getRequestDispatcher(
                        LOGIN_PAGE);

        verify(dispatcher)
                .forward(
                        request,
                        response);
    }

    /**
     * Successful login must create a session,
     * store user information and redirect.
     */
    @Test
    @DisplayName(
            "Should create a session after successful login")
    void shouldCreateSessionAfterSuccessfulLogin()
            throws Exception {

        String username =
                "reception01";

        String password =
                "Reception@123";

        when(request.getParameter("username"))
                .thenReturn(username);

        when(request.getParameter("password"))
                .thenReturn(password);

        when(request.getContextPath())
                .thenReturn(CONTEXT_PATH);

        when(request.getSession())
                .thenReturn(session);

        when(user.getUserId())
                .thenReturn(1L);

        when(user.getUsername())
                .thenReturn(username);

        when(user.getRole())
                .thenReturn("RECEPTIONIST");

        when(authenticationService.authenticate(
                username,
                password))
                .thenReturn(user);

        loginController.doPost(
                request,
                response);

        verify(authenticationService)
                .authenticate(
                        username,
                        password);

        verify(session)
                .setMaxInactiveInterval(
                        30 * 60);

        verify(session)
                .setAttribute(
                        "authenticatedUser",
                        user);

        verify(session)
                .setAttribute(
                        "userId",
                        1L);

        verify(session)
                .setAttribute(
                        "username",
                        username);

        verify(session)
                .setAttribute(
                        "userRole",
                        "RECEPTIONIST");

        verify(response)
                .sendRedirect(
                        CONTEXT_PATH + "/");
    }

    /**
     * Invalid login information must return
     * the user to the login page.
     */
    @Test
    @DisplayName(
            "Should display an error for invalid login")
    void shouldDisplayErrorForInvalidLogin()
            throws Exception {

        String username =
                "reception01";

        String password =
                "WrongPassword";

        when(request.getParameter("username"))
                .thenReturn(username);

        when(request.getParameter("password"))
                .thenReturn(password);

        when(authenticationService.authenticate(
                username,
                password))
                .thenThrow(
                        new AuthenticationException(
                                "Invalid username or password"));

        when(request.getRequestDispatcher(
                LOGIN_PAGE))
                .thenReturn(dispatcher);

        loginController.doPost(
                request,
                response);

        verify(request)
                .setAttribute(
                        "errorMessage",
                        "Invalid username or password");

        verify(request)
                .setAttribute(
                        "submittedUsername",
                        username);

        verify(dispatcher)
                .forward(
                        request,
                        response);
    }

    /**
     * A database failure must display a safe
     * message without exposing technical details.
     */
    @Test
    @DisplayName(
            "Should display a safe database error")
    void shouldDisplaySafeDatabaseError()
            throws Exception {

        String username =
                "reception01";

        String password =
                "Reception@123";

        when(request.getParameter("username"))
                .thenReturn(username);

        when(request.getParameter("password"))
                .thenReturn(password);

        when(authenticationService.authenticate(
                username,
                password))
                .thenThrow(
                        new SQLException(
                                "Database connection failed"));

        when(request.getRequestDispatcher(
                LOGIN_PAGE))
                .thenReturn(dispatcher);

        loginController.doPost(
                request,
                response);

        verify(request)
                .setAttribute(
                        "errorMessage",
                        "Login could not be completed. "
                        + "Please try again.");

        verify(request)
                .setAttribute(
                        "submittedUsername",
                        username);

        verify(dispatcher)
                .forward(
                        request,
                        response);
    }

    /**
     * An already authenticated user must be
     * redirected away from the login page.
     */
    @Test
    @DisplayName(
            "Should redirect an already logged-in user")
    void shouldRedirectAlreadyLoggedInUser()
            throws Exception {

        when(request.getSession(false))
                .thenReturn(session);

        when(session.getAttribute(
                "authenticatedUser"))
                .thenReturn(user);

        when(request.getContextPath())
                .thenReturn(CONTEXT_PATH);

        loginController.doGet(
                request,
                response);

        verify(response)
                .sendRedirect(
                        CONTEXT_PATH + "/");
    }
}