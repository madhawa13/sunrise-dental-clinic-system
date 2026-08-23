package lk.icbt.dental.controller;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles requests for application
 * help and user guidance.
 */
@WebServlet("/help")
public class HelpController
        extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String HELP_PAGE =
            "/WEB-INF/views/help.jsp";

    /**
     * Displays the application Help page.
     */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        RequestDispatcher dispatcher =
                request.getRequestDispatcher(
                        HELP_PAGE);

        dispatcher.forward(
                request,
                response);
    }
}