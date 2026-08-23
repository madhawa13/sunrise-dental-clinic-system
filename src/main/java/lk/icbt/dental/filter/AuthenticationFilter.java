package lk.icbt.dental.filter;

import java.io.IOException;

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
 * Protects application pages from
 * unauthenticated access.
 */
@WebFilter("/*")
public class AuthenticationFilter
        implements Filter {

    private static final String
            AUTHENTICATED_USER_ATTRIBUTE =
            "authenticatedUser";

    /**
     * Allows public resources and protects
     * every other application request.
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

        String contextPath =
                request.getContextPath();

        String requestUri =
                request.getRequestURI();

        String applicationPath =
                requestUri.substring(
                        contextPath.length());

        if (isPublicResource(applicationPath)) {

            chain.doFilter(
                    request,
                    response);

            return;
        }

        HttpSession session =
                request.getSession(false);

        boolean authenticated =
                session != null
                && session.getAttribute(
                        AUTHENTICATED_USER_ATTRIBUTE)
                        != null;

        if (!authenticated) {

            response.sendRedirect(
                    contextPath
                    + "/login?session=expired");

            return;
        }

        addSecurityHeaders(response);

        chain.doFilter(
                request,
                response);
    }

    /**
     * Identifies resources that may be accessed
     * without authentication.
     */
    private boolean isPublicResource(
            String applicationPath) {

        if (applicationPath == null) {
            return false;
        }

        return applicationPath.equals("/login")
                || applicationPath.equals(
                        "/favicon.ico")
                || applicationPath.startsWith(
                        "/assets/")
                || applicationPath.startsWith(
                        "/error/");
    }

    /**
     * Prevents protected pages from being stored
     * in the browser cache after logout.
     */
    private void addSecurityHeaders(
            HttpServletResponse response) {

        response.setHeader(
                "Cache-Control",
                "no-cache, no-store, "
                + "must-revalidate");

        response.setHeader(
                "Pragma",
                "no-cache");

        response.setDateHeader(
                "Expires",
                0);
    }
}