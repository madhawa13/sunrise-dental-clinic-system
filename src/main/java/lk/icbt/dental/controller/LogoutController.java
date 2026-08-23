package lk.icbt.dental.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Handles staff logout requests.
 */
@WebServlet("/logout")
public class LogoutController
        extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * Ends the authenticated user session
     * and redirects to the login page.
     */
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session =
                request.getSession(false);

        if (session != null) {

            session.invalidate();
        }

        response.sendRedirect(
                request.getContextPath()
                + "/login?logout=success");
    }

    /**
     * Supports direct navigation to the
     * logout URL.
     */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        doPost(
                request,
                response);
    }
}