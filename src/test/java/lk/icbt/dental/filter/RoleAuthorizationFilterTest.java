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
 * Tests role-based application authorization.
 */
class RoleAuthorizationFilterTest {

    private static final String CONTEXT_PATH =
            "/sunrise-dental-clinic-system";

    private HttpServletRequest request;

    private HttpServletResponse response;

    private HttpSession session;

    private FilterChain filterChain;

    private RoleAuthorizationFilter
            authorizationFilter;

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

        filterChain =
                mock(FilterChain.class);

        authorizationFilter =
                new RoleAuthorizationFilter();

        when(request.getContextPath())
                .thenReturn(CONTEXT_PATH);

        when(request.getSession(false))
                .thenReturn(session);
    }

    /**
     * Receptionists cannot manage treatments.
     */
    @Test
    @DisplayName(
            "Should block receptionist from treatments")
    void shouldBlockReceptionistFromTreatments()
            throws Exception {

        prepareRequest(
                "/treatments",
                "RECEPTIONIST");

        authorizationFilter.doFilter(
                request,
                response,
                filterChain);

        verifyForbiddenResponse();
    }

    /**
     * Receptionists cannot manage
     * treatment charge details.
     */
    @Test
    @DisplayName(
            "Should block receptionist from treatment charges")
    void shouldBlockReceptionistFromTreatmentCharges()
            throws Exception {

        prepareRequest(
                "/treatment-details",
                "RECEPTIONIST");

        authorizationFilter.doFilter(
                request,
                response,
                filterChain);

        verifyForbiddenResponse();
    }

    /**
     * Dentists may access treatment management.
     */
    @Test
    @DisplayName(
            "Should allow dentist to access treatments")
    void shouldAllowDentistToAccessTreatments()
            throws Exception {

        prepareRequest(
                "/treatments",
                "DENTIST");

        authorizationFilter.doFilter(
                request,
                response,
                filterChain);

        verify(filterChain)
                .doFilter(
                        request,
                        response);
    }

    /**
     * Dentists cannot manage bills.
     */
    @Test
    @DisplayName(
            "Should block dentist from billing")
    void shouldBlockDentistFromBilling()
            throws Exception {

        prepareRequest(
                "/bills",
                "DENTIST");

        authorizationFilter.doFilter(
                request,
                response,
                filterChain);

        verifyForbiddenResponse();
    }

    /**
     * Receptionists may access billing.
     */
    @Test
    @DisplayName(
            "Should allow receptionist to access billing")
    void shouldAllowReceptionistToAccessBilling()
            throws Exception {

        prepareRequest(
                "/bills",
                "RECEPTIONIST");

        authorizationFilter.doFilter(
                request,
                response,
                filterChain);

        verify(filterChain)
                .doFilter(
                        request,
                        response);
    }

    /**
     * A request without role information
     * must return to login.
     */
    @Test
    @DisplayName(
            "Should redirect request without user role")
    void shouldRedirectRequestWithoutRole()
            throws Exception {

        when(request.getRequestURI())
                .thenReturn(
                        CONTEXT_PATH
                        + "/bills");

        when(request.getSession(false))
                .thenReturn(null);

        authorizationFilter.doFilter(
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
     * Configures the requested module and role.
     */
    private void prepareRequest(
            String applicationPath,
            String role) {

        when(request.getRequestURI())
                .thenReturn(
                        CONTEXT_PATH
                        + applicationPath);

        when(session.getAttribute("userRole"))
                .thenReturn(role);
    }

    /**
     * Verifies the standard forbidden response.
     */
    private void verifyForbiddenResponse()
            throws Exception {

        verify(response)
                .sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You do not have permission "
                        + "to access this page");

        verify(filterChain, never())
                .doFilter(
                        request,
                        response);
    }
}