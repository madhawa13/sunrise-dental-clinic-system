package lk.icbt.dental.controller;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import lk.icbt.dental.exception.AuthenticationException;
import lk.icbt.dental.model.User;
import lk.icbt.dental.service.AuthenticationService;
import lk.icbt.dental.service.AuthenticationServiceImpl;

/**
 * Handles user login requests.
 */
@WebServlet("/login")
public class LoginController
        extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String LOGIN_PAGE =
            "/WEB-INF/views/auth/login.jsp";

    private static final int
            SESSION_TIMEOUT_SECONDS =
            30 * 60;

    private final AuthenticationService
            authenticationService;

    /**
     * Constructor used by Tomcat.
     */
    public LoginController() {

        this(new AuthenticationServiceImpl());
    }

    /**
     * Constructor used by automated tests.
     *
     * @param authenticationService authentication service
     */
    LoginController(
            AuthenticationService
                    authenticationService) {

        if (authenticationService == null) {

            throw new IllegalArgumentException(
                    "Authentication service cannot be null");
        }

        this.authenticationService =
                authenticationService;
    }

    /**
     * Displays the login page.
     */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession existingSession =
                request.getSession(false);

        if (existingSession != null
                && existingSession.getAttribute(
                        "authenticatedUser") != null) {

            response.sendRedirect(
                    request.getContextPath() + "/");

            return;
        }

        forward(
                request,
                response,
                LOGIN_PAGE);
    }

    /**
     * Authenticates submitted login information.
     */
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String username =
                request.getParameter("username");

        String password =
                request.getParameter("password");

        try {
            User authenticatedUser =
                    authenticationService
                            .authenticate(
                                    username,
                                    password);

            HttpSession session =
                    request.getSession();

            session.setMaxInactiveInterval(
                    SESSION_TIMEOUT_SECONDS);

            session.setAttribute(
                    "authenticatedUser",
                    authenticatedUser);

            session.setAttribute(
                    "userId",
                    authenticatedUser.getUserId());

            session.setAttribute(
                    "username",
                    authenticatedUser.getUsername());

            session.setAttribute(
                    "userRole",
                    authenticatedUser.getRole());

            response.sendRedirect(
                    request.getContextPath() + "/");

        } catch (AuthenticationException exception) {

            request.setAttribute(
                    "errorMessage",
                    exception.getMessage());

            request.setAttribute(
                    "submittedUsername",
                    username);

            forward(
                    request,
                    response,
                    LOGIN_PAGE);

        } catch (SQLException exception) {

            request.setAttribute(
                    "errorMessage",
                    "Login could not be completed. "
                    + "Please try again.");

            request.setAttribute(
                    "submittedUsername",
                    username);

            forward(
                    request,
                    response,
                    LOGIN_PAGE);
        }
    }

    /**
     * Forwards a request to a JSP page.
     */
    private void forward(
            HttpServletRequest request,
            HttpServletResponse response,
            String page)
            throws ServletException, IOException {

        RequestDispatcher dispatcher =
                request.getRequestDispatcher(page);

        dispatcher.forward(
                request,
                response);
    }
}