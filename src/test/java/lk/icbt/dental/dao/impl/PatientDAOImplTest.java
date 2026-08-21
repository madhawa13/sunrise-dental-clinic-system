package lk.icbt.dental.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lk.icbt.dental.dao.PatientDAO;
import lk.icbt.dental.model.Patient;

class PatientDAOImplTest {

    private static final String TEST_DATABASE_URL =
            "jdbc:h2:mem:patient_test;"
            + "MODE=MySQL;"
            + "DB_CLOSE_DELAY=-1";

    private Connection databaseKeeper;
    private PatientDAO patientDAO;

    @BeforeEach
    void setUp() throws Exception {

        databaseKeeper = DriverManager.getConnection(
                TEST_DATABASE_URL);

        createPatientTable(databaseKeeper);

        patientDAO = new PatientDAOImpl(
                () -> DriverManager.getConnection(
                        TEST_DATABASE_URL));
    }

    @AfterEach
    void tearDown() throws Exception {

        if (databaseKeeper != null
                && !databaseKeeper.isClosed()) {

            try (Statement statement =
                    databaseKeeper.createStatement()) {

                statement.execute(
                        "DROP TABLE IF EXISTS patients");
            }

            databaseKeeper.close();
        }
    }

    @Test
    @DisplayName("Should save a patient and return generated ID")
    void shouldSavePatientAndReturnGeneratedId()
            throws Exception {

        Patient patient = createPatient(
                "PAT-0001",
                "Nimal",
                "Perera",
                "901234567V",
                "0771234567");

        long generatedPatientId =
                patientDAO.save(patient);

        assertTrue(generatedPatientId > 0);

        assertEquals(
                generatedPatientId,
                patient.getPatientId());
    }

    @Test
    @DisplayName("Should find a patient using patient ID")
    void shouldFindPatientById() throws Exception {

        Patient patient = createPatient(
                "PAT-0002",
                "Kamal",
                "Silva",
                "851234567V",
                "0712345678");

        long generatedPatientId =
                patientDAO.save(patient);

        Patient savedPatient =
                patientDAO.findById(generatedPatientId)
                        .orElseThrow();

        assertEquals(
                generatedPatientId,
                savedPatient.getPatientId());

        assertEquals(
                "PAT-0002",
                savedPatient.getPatientNumber());

        assertEquals(
                "Kamal",
                savedPatient.getFirstName());

        assertEquals(
                "Silva",
                savedPatient.getLastName());
    }

    @Test
    @DisplayName("Should find a patient using patient number")
    void shouldFindPatientByPatientNumber()
            throws Exception {

        Patient patient = createPatient(
                "PAT-0003",
                "Sunil",
                "Fernando",
                "881234567V",
                "0751234567");

        patientDAO.save(patient);

        Patient savedPatient =
                patientDAO.findByPatientNumber("PAT-0003")
                        .orElseThrow();

        assertEquals(
                "PAT-0003",
                savedPatient.getPatientNumber());

        assertEquals(
                "Sunil",
                savedPatient.getFirstName());
    }

    @Test
    @DisplayName("Should return all active patients")
    void shouldReturnAllActivePatients()
            throws Exception {

        patientDAO.save(createPatient(
                "PAT-0004",
                "Amal",
                "Perera",
                "921234567V",
                "0761234567"));

        patientDAO.save(createPatient(
                "PAT-0005",
                "Nimali",
                "Kumari",
                "935678901V",
                "0781234567"));

        List<Patient> patients =
                patientDAO.findAll();

        assertEquals(2, patients.size());
    }

    @Test
    @DisplayName("Should search patients using a name")
    void shouldSearchPatientsUsingName()
            throws Exception {

        patientDAO.save(createPatient(
                "PAT-0006",
                "Kasun",
                "Jayasinghe",
                "941234567V",
                "0701234567"));

        patientDAO.save(createPatient(
                "PAT-0007",
                "Malith",
                "Perera",
                "951234567V",
                "0721234567"));

        List<Patient> searchResults =
                patientDAO.search("Kasun");

        assertEquals(1, searchResults.size());

        assertEquals(
                "PAT-0006",
                searchResults.get(0).getPatientNumber());
    }

    @Test
    @DisplayName("Should update an existing patient")
    void shouldUpdateExistingPatient()
            throws Exception {

        Patient patient = createPatient(
                "PAT-0008",
                "Saman",
                "Bandara",
                "861234567V",
                "0741234567");

        long generatedPatientId =
                patientDAO.save(patient);

        patient.setPatientId(generatedPatientId);
        patient.setPhone("0779999999");
        patient.setAddress("Peradeniya");
        patient.setMedicalNotes("Allergic to penicillin");

        boolean updated =
                patientDAO.update(patient);

        Patient updatedPatient =
                patientDAO.findById(generatedPatientId)
                        .orElseThrow();

        assertTrue(updated);

        assertEquals(
                "0779999999",
                updatedPatient.getPhone());

        assertEquals(
                "Peradeniya",
                updatedPatient.getAddress());

        assertEquals(
                "Allergic to penicillin",
                updatedPatient.getMedicalNotes());
    }

    @Test
    @DisplayName("Should soft delete an existing patient")
    void shouldSoftDeleteExistingPatient()
            throws Exception {

        Patient patient = createPatient(
                "PAT-0009",
                "Ruwan",
                "Dissanayake",
                "891234567V",
                "0778888888");

        long generatedPatientId =
                patientDAO.save(patient);

        boolean deleted =
                patientDAO.delete(generatedPatientId);

        assertTrue(deleted);

        assertFalse(
                patientDAO.findById(generatedPatientId)
                        .isPresent());
    }

    /**
     * Creates a Patient object for a test.
     */
    private Patient createPatient(
            String patientNumber,
            String firstName,
            String lastName,
            String nicNumber,
            String phone) {

        return new Patient(
                patientNumber,
                firstName,
                lastName,
                LocalDate.of(1990, 1, 1),
                "MALE",
                nicNumber,
                phone,
                firstName.toLowerCase()
                        + "@example.com",
                "Kandy",
                "No known allergies");
    }

    /**
     * Creates the H2 patients table.
     */
    private void createPatientTable(Connection connection)
            throws Exception {

        String createTableSql = """
                CREATE TABLE IF NOT EXISTS patients (
                    patient_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    patient_number VARCHAR(20) NOT NULL UNIQUE,
                    first_name VARCHAR(50) NOT NULL,
                    last_name VARCHAR(50) NOT NULL,
                    date_of_birth DATE NOT NULL,
                    gender VARCHAR(10) NOT NULL,
                    nic_number VARCHAR(20) UNIQUE,
                    phone VARCHAR(20) NOT NULL,
                    email VARCHAR(100),
                    address VARCHAR(255),
                    medical_notes CLOB,
                    active BOOLEAN NOT NULL DEFAULT TRUE,
                    created_at TIMESTAMP NOT NULL
                        DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP NOT NULL
                        DEFAULT CURRENT_TIMESTAMP
                )
                """;

        try (Statement statement =
                connection.createStatement()) {

            statement.execute(createTableSql);
        }
    }
}