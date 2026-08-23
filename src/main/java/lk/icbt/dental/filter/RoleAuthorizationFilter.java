package lk.icbt.dental.filter;

import java.io.IOException;
import java.util.Locale;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Restricts treatment and billing modules
 * according to the authenticated staff role.
 */
@WebFilter(
        urlPatterns = {
                "/treatments",
                "/treatment-details",
                "/bills",
                "/payments"
        })
public class RoleAuthorizationFilter
        implements Filter {

    private static final String
            ROLE_RECEPTIONIST =
            "RECEPTIONIST";

    private static final String
            ROLE_DENTIST =
            "DENTIST";

    /**
     * Checks whether the authenticated role
     * may access the requested module.
     */
    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request =
                (HttpServletRequest)
                        servletRequest;

        HttpServletResponse response =
                (HttpServletResponse)
                        servletResponse;

        HttpSession session =
                request.getSession(false);

        if (session == null
                || session.getAttribute(
                        "userRole") == null) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/login?session=expired");

            return;
        }

        String userRole =
                session.getAttribute(
                        "userRole")
                        .toString()
                        .trim()
                        .toUpperCase(Locale.ROOT);

        String requestPath =
                getApplicationPath(request);

        boolean treatmentModule =
                requestPath.equals("/treatments")
                || requestPath.equals(
                        "/treatment-details");

        boolean billingModule =
                requestPath.equals("/bills")
                || requestPath.equals(
                        "/payments");

        if (treatmentModule
                && !ROLE_DENTIST.equals(
                        userRole)) {

            denyAccess(response);

            return;
        }

        if (billingModule
                && !ROLE_RECEPTIONIST.equals(
                        userRole)) {

            denyAccess(response);

            return;
        }

        chain.doFilter(
                request,
                response);
    }

    /**
     * Returns the requested path without
     * the application context path.
     */
    private String getApplicationPath(
            HttpServletRequest request) {

        String contextPath =
                request.getContextPath();

        String requestUri =
                request.getRequestURI();

        return requestUri.substring(
                contextPath.length());
    }

    /**
     * Returns an HTTP 403 response.
     */
    private void denyAccess(
            HttpServletResponse response)
            throws IOException {

        response.sendError(
                HttpServletResponse.SC_FORBIDDEN,
                "You do not have permission "
                + "to access this page");
    }
}