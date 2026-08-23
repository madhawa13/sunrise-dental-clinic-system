package lk.icbt.dental.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lk.icbt.dental.model.Treatment;
import lk.icbt.dental.model.TreatmentCharge;
import lk.icbt.dental.model.TreatmentDetail;
import lk.icbt.dental.service.TreatmentDetailService;
import lk.icbt.dental.service.TreatmentService;

/**
 * Automated tests for TreatmentDetailController.
 */
class TreatmentDetailControllerTest {

    private TreatmentDetailService
            treatmentDetailService;

    private TreatmentService treatmentService;

    private TreatmentDetailController controller;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() {

        treatmentDetailService =
                mock(TreatmentDetailService.class);

        treatmentService =
                mock(TreatmentService.class);

        request =
                mock(HttpServletRequest.class);

        response =
                mock(HttpServletResponse.class);

        dispatcher =
                mock(RequestDispatcher.class);

        controller =
                new TreatmentDetailController(
                        treatmentDetailService,
                        treatmentService);
    }

    @Test
    @DisplayName(
            "Should display charge management page for treatment")
    void shouldDisplayChargeManagementPageForTreatment()
            throws Exception {

        Treatment treatment =
                new Treatment();

        treatment.setTreatmentId(50L);

        List<TreatmentDetail> details =
                List.of(new TreatmentDetail());

        List<TreatmentCharge> charges =
                List.of(new TreatmentCharge());

        when(request.getParameter(
                "treatmentId"))
                .thenReturn("50");

        when(treatmentService
                .getTreatmentById(50L))
                .thenReturn(treatment);

        when(treatmentDetailService
                .getDetailsByTreatmentId(50L))
                .thenReturn(details);

        when(treatmentDetailService
                .getActiveTreatmentCharges())
                .thenReturn(charges);

        when(treatmentDetailService
                .calculateTreatmentTotal(50L))
                .thenReturn(
                        new BigDecimal("3500.00"));

        when(request.getRequestDispatcher(
                "/WEB-INF/views/treatment/charges.jsp"))
                .thenReturn(dispatcher);

        controller.doGet(
                request,
                response);

        verify(request).setAttribute(
                "treatment",
                treatment);

        verify(request).setAttribute(
                "details",
                details);

        verify(request).setAttribute(
                "charges",
                charges);

        verify(request).setAttribute(
                "treatmentTotal",
                new BigDecimal("3500.00"));

        verify(dispatcher).forward(
                request,
                response);
    }

    @Test
    @DisplayName(
            "Should add selected charge and redirect")
    void shouldAddSelectedChargeAndRedirect()
            throws Exception {

        when(request.getParameter("action"))
                .thenReturn("add");

        when(request.getParameter(
                "treatmentId"))
                .thenReturn("50");

        when(request.getParameter("chargeId"))
                .thenReturn("2");

        when(request.getParameter("quantity"))
                .thenReturn("3");

        when(request.getParameter("notes"))
                .thenReturn(
                        "Dental cleaning");

        when(request.getContextPath())
                .thenReturn(
                        "/sunrise-dental-clinic-system");

        controller.doPost(
                request,
                response);

        verify(treatmentDetailService)
                .addTreatmentCharge(
                        50L,
                        2L,
                        3,
                        "Dental cleaning");

        verify(response).sendRedirect(
                "/sunrise-dental-clinic-system"
                + "/treatment-details"
                + "?treatmentId=50"
                + "&success=added");
    }

    @Test
    @DisplayName(
            "Should delete charge item and redirect")
    void shouldDeleteChargeItemAndRedirect()
            throws Exception {

        when(request.getParameter("action"))
                .thenReturn("delete");

        when(request.getParameter(
                "treatmentDetailId"))
                .thenReturn("100");

        when(request.getParameter(
                "treatmentId"))
                .thenReturn("50");

        when(request.getContextPath())
                .thenReturn(
                        "/sunrise-dental-clinic-system");

        controller.doPost(
                request,
                response);

        verify(treatmentDetailService)
                .deleteTreatmentCharge(100L);

        verify(response).sendRedirect(
                "/sunrise-dental-clinic-system"
                + "/treatment-details"
                + "?treatmentId=50"
                + "&success=deleted");
    }
}