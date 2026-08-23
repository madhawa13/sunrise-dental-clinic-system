package lk.icbt.dental.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Tests the application Help controller.
 */
class HelpControllerTest {

    private static final String HELP_PAGE =
            "/WEB-INF/views/help.jsp";

    private HttpServletRequest request;

    private HttpServletResponse response;

    private RequestDispatcher dispatcher;

    private HelpController helpController;

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

        dispatcher =
                mock(RequestDispatcher.class);

        helpController =
                new HelpController();
    }

    /**
     * RED test:
     * The controller must display
     * the application Help page.
     */
    @Test
    @DisplayName(
            "Should display application help page")
    void shouldDisplayApplicationHelpPage()
            throws Exception {

        when(request.getRequestDispatcher(
                HELP_PAGE))
                .thenReturn(dispatcher);

        helpController.doGet(
                request,
                response);

        verify(request)
                .getRequestDispatcher(
                        HELP_PAGE);

        verify(dispatcher)
                .forward(
                        request,
                        response);
    }
}