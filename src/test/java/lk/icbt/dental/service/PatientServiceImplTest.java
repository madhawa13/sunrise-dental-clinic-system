package lk.icbt.dental.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lk.icbt.dental.dao.PatientDAO;
import lk.icbt.dental.exception.PatientNotFoundException;
import lk.icbt.dental.exception.PatientValidationException;
import lk.icbt.dental.model.Patient;

class PatientServiceImplTest {

    private PatientDAO patientDAO;
    private PatientService patientService;

    @BeforeEach
    void setUp() {

        /*
         * Mockito creates a fake DAO.
         * Therefore, these tests do not use MySQL or H2.
         */
        patientDAO = mock(PatientDAO.class);

        patientService =
                new PatientServiceImpl(patientDAO);
    }

    @Test
    @DisplayName(
            "Should validate, generate number and register patient")
    void shouldRegisterPatient() throws Exception {

        Patient patient = createPatient();
        patient.setPatientNumber(null);

        when(patientDAO.save(patient))
                .thenReturn(10L);

        Patient registeredPatient =
                patientService.registerPatient(patient);

        assertSame(
                patient,
                registeredPatient);

        assertEquals(
                10L,
                registeredPatient.getPatientId());

        assertTrue(
                registeredPatient.getPatientNumber()
                        .startsWith("PAT-"));

        verify(patientDAO).save(patient);
    }

    @Test
    @DisplayName(
            "Should reject patient with blank first name")
    void shouldRejectBlankFirstName() throws Exception {

        Patient patient = createPatient();
        patient.setFirstName(" ");

        assertThrows(
                PatientValidationException.class,
                () -> patientService.registerPatient(patient));

        verifyNoInteractions(patientDAO);
    }

    @Test
    @DisplayName("Should return patient by ID")
    void shouldReturnPatientById() throws Exception {

        Patient patient = createPatient();
        patient.setPatientId(5L);

        when(patientDAO.findById(5L))
                .thenReturn(Optional.of(patient));

        Patient result =
                patientService.getPatientById(5L);

        assertSame(patient, result);

        verify(patientDAO).findById(5L);
    }

    @Test
    @DisplayName(
            "Should throw exception when patient is not found")
    void shouldThrowWhenPatientIsNotFound()
            throws Exception {

        when(patientDAO.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                PatientNotFoundException.class,
                () -> patientService.getPatientById(99L));

        verify(patientDAO).findById(99L);
    }

    @Test
    @DisplayName("Should return all active patients")
    void shouldReturnAllPatients() throws Exception {

        List<Patient> patients =
                List.of(
                        createPatient(),
                        createPatient());

        when(patientDAO.findAll())
                .thenReturn(patients);

        List<Patient> result =
                patientService.getAllPatients();

        assertEquals(2, result.size());

        assertSame(patients, result);

        verify(patientDAO).findAll();
    }

    @Test
    @DisplayName("Should delegate patient search to DAO")
    void shouldSearchPatients() throws Exception {

        List<Patient> searchResults =
                List.of(createPatient());

        when(patientDAO.search("Kamal"))
                .thenReturn(searchResults);

        List<Patient> result =
                patientService.searchPatients("  Kamal  ");

        assertSame(searchResults, result);

        verify(patientDAO).search("Kamal");

        verify(
                patientDAO,
                never()).findAll();
    }

    @Test
    @DisplayName(
            "Should return all patients for blank search")
    void shouldReturnAllForBlankSearch()
            throws Exception {

        List<Patient> patients =
                List.of(createPatient());

        when(patientDAO.findAll())
                .thenReturn(patients);

        List<Patient> result =
                patientService.searchPatients(" ");

        assertSame(patients, result);

        verify(patientDAO).findAll();

        verify(
                patientDAO,
                never()).search(any());
    }

    @Test
    @DisplayName("Should update an existing patient")
    void shouldUpdateExistingPatient()
            throws Exception {

        Patient patient = createPatient();
        patient.setPatientId(15L);

        when(patientDAO.update(patient))
                .thenReturn(true);

        Patient updatedPatient =
                patientService.updatePatient(patient);

        assertSame(patient, updatedPatient);

        verify(patientDAO).update(patient);
    }

    @Test
    @DisplayName(
            "Should throw exception when update target is missing")
    void shouldThrowWhenUpdateTargetIsMissing()
            throws Exception {

        Patient patient = createPatient();
        patient.setPatientId(100L);

        when(patientDAO.update(patient))
                .thenReturn(false);

        assertThrows(
                PatientNotFoundException.class,
                () -> patientService.updatePatient(patient));

        verify(patientDAO).update(patient);
    }

    @Test
    @DisplayName("Should soft delete an existing patient")
    void shouldDeleteExistingPatient()
            throws Exception {

        when(patientDAO.delete(25L))
                .thenReturn(true);

        patientService.deletePatient(25L);

        verify(patientDAO).delete(25L);
    }

    @Test
    @DisplayName(
            "Should throw exception when delete target is missing")
    void shouldThrowWhenDeleteTargetIsMissing()
            throws Exception {

        when(patientDAO.delete(200L))
                .thenReturn(false);

        assertThrows(
                PatientNotFoundException.class,
                () -> patientService.deletePatient(200L));

        verify(patientDAO).delete(200L);
    }

    /**
     * Creates a valid Patient used by service tests.
     */
    private Patient createPatient() {

        return new Patient(
                "PAT-TEST-001",
                "Kamal",
                "Perera",
                LocalDate.of(1990, 5, 10),
                "MALE",
                "901234567V",
                "0771234567",
                "kamal@example.com",
                "Kandy",
                "No known allergies");
    }
}