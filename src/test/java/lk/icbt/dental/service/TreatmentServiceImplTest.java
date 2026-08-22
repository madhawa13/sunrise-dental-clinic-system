package lk.icbt.dental.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lk.icbt.dental.dao.TreatmentDAO;
import lk.icbt.dental.exception.TreatmentNotFoundException;
import lk.icbt.dental.exception.TreatmentValidationException;
import lk.icbt.dental.model.Treatment;

/**
 * Tests TreatmentServiceImpl business logic
 * using a Mockito TreatmentDAO.
 */
class TreatmentServiceImplTest {

    private TreatmentDAO treatmentDAO;

    private TreatmentService treatmentService;

    @BeforeEach
    void setUp() {

        treatmentDAO =
                mock(TreatmentDAO.class);

        treatmentService =
                new TreatmentServiceImpl(
                        treatmentDAO);
    }

    @Test
    @DisplayName(
            "Should create a valid treatment")
    void shouldCreateValidTreatment()
            throws Exception {

        Treatment treatment =
                createTreatment();

        when(treatmentDAO.save(treatment))
                .thenReturn(101L);

        Treatment savedTreatment =
                treatmentService.createTreatment(
                        treatment);

        assertNotNull(savedTreatment);

        assertEquals(
                Long.valueOf(101L),
                savedTreatment.getTreatmentId());

        verify(treatmentDAO)
                .save(treatment);
    }

    @Test
    @DisplayName(
            "Should reject treatment with blank notes")
    void shouldRejectTreatmentWithBlankNotes() {

        Treatment treatment =
                createTreatment();

        treatment.setTreatmentNotes(" ");

        assertThrows(
                TreatmentValidationException.class,
                () -> treatmentService
                        .createTreatment(treatment));

        try {
            verify(treatmentDAO, never())
                    .save(treatment);
        } catch (Exception exception) {
            throw new AssertionError(exception);
        }
    }

    @Test
    @DisplayName(
            "Should reject a future treatment date")
    void shouldRejectFutureTreatmentDate() {

        Treatment treatment =
                createTreatment();

        treatment.setTreatmentDate(
                LocalDate.now().plusDays(1));

        assertThrows(
                TreatmentValidationException.class,
                () -> treatmentService
                        .createTreatment(treatment));
    }

    @Test
    @DisplayName(
            "Should return treatment by ID")
    void shouldReturnTreatmentById()
            throws Exception {

        Treatment treatment =
                createTreatment();

        treatment.setTreatmentId(101L);

        when(treatmentDAO.findById(101L))
                .thenReturn(
                        Optional.of(treatment));

        Treatment result =
                treatmentService
                        .getTreatmentById(101L);

        assertSame(treatment, result);

        verify(treatmentDAO)
                .findById(101L);
    }

    @Test
    @DisplayName(
            "Should throw exception when treatment is missing")
    void shouldThrowExceptionWhenTreatmentIsMissing()
            throws Exception {

        when(treatmentDAO.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                TreatmentNotFoundException.class,
                () -> treatmentService
                        .getTreatmentById(999L));
    }

    @Test
    @DisplayName(
            "Should return all treatments")
    void shouldReturnAllTreatments()
            throws Exception {

        List<Treatment> treatments =
                List.of(createTreatment());

        when(treatmentDAO.findAll())
                .thenReturn(treatments);

        List<Treatment> result =
                treatmentService
                        .getAllTreatments();

        assertSame(treatments, result);

        verify(treatmentDAO).findAll();
    }

    @Test
    @DisplayName(
            "Should return treatments using appointment ID")
    void shouldReturnTreatmentsUsingAppointmentId()
            throws Exception {

        List<Treatment> treatments =
                List.of(createTreatment());

        when(treatmentDAO.findByAppointmentId(1L))
                .thenReturn(treatments);

        List<Treatment> result =
                treatmentService
                        .getTreatmentsByAppointmentId(1L);

        assertSame(treatments, result);

        verify(treatmentDAO)
                .findByAppointmentId(1L);
    }

    @Test
    @DisplayName(
            "Should return treatments using date")
    void shouldReturnTreatmentsUsingDate()
            throws Exception {

        LocalDate treatmentDate =
                LocalDate.now();

        List<Treatment> treatments =
                List.of(createTreatment());

        when(treatmentDAO.findByDate(treatmentDate))
                .thenReturn(treatments);

        List<Treatment> result =
                treatmentService
                        .getTreatmentsByDate(
                                treatmentDate);

        assertSame(treatments, result);

        verify(treatmentDAO)
                .findByDate(treatmentDate);
    }

    @Test
    @DisplayName(
            "Should delegate treatment search to DAO")
    void shouldDelegateTreatmentSearchToDao()
            throws Exception {

        List<Treatment> treatments =
                List.of(createTreatment());

        when(treatmentDAO.search("cavity"))
                .thenReturn(treatments);

        List<Treatment> result =
                treatmentService
                        .searchTreatments(
                                " cavity ");

        assertSame(treatments, result);

        verify(treatmentDAO)
                .search("cavity");
    }

    @Test
    @DisplayName(
            "Should return all treatments for blank search")
    void shouldReturnAllTreatmentsForBlankSearch()
            throws Exception {

        List<Treatment> treatments =
                List.of(createTreatment());

        when(treatmentDAO.findAll())
                .thenReturn(treatments);

        List<Treatment> result =
                treatmentService
                        .searchTreatments(" ");

        assertSame(treatments, result);

        verify(treatmentDAO).findAll();

        verify(treatmentDAO, never())
                .search(" ");
    }

    @Test
    @DisplayName(
            "Should update an existing treatment")
    void shouldUpdateExistingTreatment()
            throws Exception {

        Treatment treatment =
                createTreatment();

        treatment.setTreatmentId(101L);

        when(treatmentDAO.update(treatment))
                .thenReturn(true);

        Treatment result =
                treatmentService
                        .updateTreatment(treatment);

        assertSame(treatment, result);

        verify(treatmentDAO)
                .update(treatment);
    }

    @Test
    @DisplayName(
            "Should throw exception when update target is missing")
    void shouldThrowExceptionWhenUpdateTargetIsMissing()
            throws Exception {

        Treatment treatment =
                createTreatment();

        treatment.setTreatmentId(999L);

        when(treatmentDAO.update(treatment))
                .thenReturn(false);

        assertThrows(
                TreatmentNotFoundException.class,
                () -> treatmentService
                        .updateTreatment(treatment));
    }

    @Test
    @DisplayName(
            "Should delete an existing treatment")
    void shouldDeleteExistingTreatment()
            throws Exception {

        when(treatmentDAO.delete(101L))
                .thenReturn(true);

        treatmentService.deleteTreatment(101L);

        verify(treatmentDAO).delete(101L);
    }

    @Test
    @DisplayName(
            "Should throw exception when delete target is missing")
    void shouldThrowExceptionWhenDeleteTargetIsMissing()
            throws Exception {

        when(treatmentDAO.delete(999L))
                .thenReturn(false);

        assertThrows(
                TreatmentNotFoundException.class,
                () -> treatmentService
                        .deleteTreatment(999L));
    }

    private Treatment createTreatment() {

        return new Treatment(
                1L,
                2L,
                LocalDate.now(),
                "Dental cavity",
                "Dental examination and tooth filling",
                "Pain relief medication");
    }
}