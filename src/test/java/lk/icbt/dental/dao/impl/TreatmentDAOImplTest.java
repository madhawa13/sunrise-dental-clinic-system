package lk.icbt.dental.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lk.icbt.dental.dao.TreatmentDAO;
import lk.icbt.dental.model.Treatment;
import lk.icbt.dental.util.ConnectionProvider;

/**
 * Tests TreatmentDAOImpl with an isolated
 * in-memory H2 database.
 */
class TreatmentDAOImplTest {

    private static final String TEST_DATABASE_URL =
            "jdbc:h2:mem:treatment_test;"
            + "MODE=MySQL;"
            + "DB_CLOSE_DELAY=-1;"
            + "DATABASE_TO_LOWER=TRUE";

    private static final String TEST_DATABASE_USER = "sa";

    private static final String TEST_DATABASE_PASSWORD = "";

    private Connection databaseKeeper;

    private TreatmentDAO treatmentDAO;

    @BeforeEach
    void setUp() throws Exception {

        databaseKeeper =
                DriverManager.getConnection(
                        TEST_DATABASE_URL,
                        TEST_DATABASE_USER,
                        TEST_DATABASE_PASSWORD);

        createDatabaseTables();

        insertRequiredTestRecords();

        ConnectionProvider connectionProvider =
                () -> DriverManager.getConnection(
                        TEST_DATABASE_URL,
                        TEST_DATABASE_USER,
                        TEST_DATABASE_PASSWORD);

        treatmentDAO =
                new TreatmentDAOImpl(
                        connectionProvider);
    }

    @AfterEach
    void tearDown() throws SQLException {

        if (databaseKeeper != null
                && !databaseKeeper.isClosed()) {

            databaseKeeper.close();
        }
    }

    @Test
    @DisplayName(
            "Should save a treatment and return generated ID")
    void shouldSaveTreatmentAndReturnGeneratedId()
            throws Exception {

        Treatment treatment = createTreatment();

        long generatedTreatmentId =
                treatmentDAO.save(treatment);

        assertTrue(
                generatedTreatmentId > 0,
                "Generated treatment ID should "
                + "be greater than zero");

        assertEquals(
                generatedTreatmentId,
                treatment.getTreatmentId());
    }

    @Test
    @DisplayName(
            "Should find a treatment using generated ID")
    void shouldFindTreatmentUsingGeneratedId()
            throws Exception {

        Treatment treatment = createTreatment();

        long generatedTreatmentId =
                treatmentDAO.save(treatment);

        Optional<Treatment> savedTreatment =
                treatmentDAO.findById(
                        generatedTreatmentId);

        assertTrue(savedTreatment.isPresent());

        assertEquals(
                "Dental cavity",
                savedTreatment.get().getDiagnosis());

        assertEquals(
                "APT-TEST-001",
                savedTreatment.get()
                        .getAppointmentNumber());

        assertEquals(
                "Nimal Fernando",
                savedTreatment.get()
                        .getPatientName());

        assertEquals(
                "Dr. Test Dentist",
                savedTreatment.get()
                        .getDentistName());
    }

    @Test
    @DisplayName(
            "Should return all treatment records")
    void shouldReturnAllTreatmentRecords()
            throws Exception {

        treatmentDAO.save(createTreatment());

        Treatment secondTreatment =
                new Treatment(
                        1L,
                        1L,
                        LocalDate.of(2026, 8, 26),
                        "Sensitive tooth",
                        "Applied sensitivity treatment",
                        "Sensitivity toothpaste");

        treatmentDAO.save(secondTreatment);

        List<Treatment> treatments =
                treatmentDAO.findAll();

        assertEquals(2, treatments.size());
    }

    @Test
    @DisplayName(
            "Should find treatments using appointment ID")
    void shouldFindTreatmentsUsingAppointmentId()
            throws Exception {

        treatmentDAO.save(createTreatment());

        List<Treatment> treatments =
                treatmentDAO.findByAppointmentId(1L);

        assertEquals(1, treatments.size());

        assertEquals(
                Long.valueOf(1L),
                treatments.get(0).getAppointmentId());
    }

    @Test
    @DisplayName(
            "Should find treatments using treatment date")
    void shouldFindTreatmentsUsingTreatmentDate()
            throws Exception {

        treatmentDAO.save(createTreatment());

        List<Treatment> treatments =
                treatmentDAO.findByDate(
                        LocalDate.of(2026, 8, 25));

        assertEquals(1, treatments.size());

        assertEquals(
                LocalDate.of(2026, 8, 25),
                treatments.get(0).getTreatmentDate());
    }

    @Test
    @DisplayName(
            "Should search treatment records using diagnosis")
    void shouldSearchTreatmentRecordsUsingDiagnosis()
            throws Exception {

        treatmentDAO.save(createTreatment());

        List<Treatment> treatments =
                treatmentDAO.search("cavity");

        assertEquals(1, treatments.size());

        assertEquals(
                "Dental cavity",
                treatments.get(0).getDiagnosis());
    }

    @Test
    @DisplayName(
            "Should update an existing treatment")
    void shouldUpdateExistingTreatment()
            throws Exception {

        Treatment treatment = createTreatment();

        long generatedTreatmentId =
                treatmentDAO.save(treatment);

        treatment.setTreatmentId(
                generatedTreatmentId);

        treatment.setDiagnosis(
                "Deep dental cavity");

        treatment.setTreatmentNotes(
                "Root canal assessment completed");

        treatment.setPrescription(
                "Pain relief and antibiotics");

        boolean updated =
                treatmentDAO.update(treatment);

        Optional<Treatment> updatedTreatment =
                treatmentDAO.findById(
                        generatedTreatmentId);

        assertTrue(updated);

        assertTrue(updatedTreatment.isPresent());

        assertEquals(
                "Deep dental cavity",
                updatedTreatment.get()
                        .getDiagnosis());

        assertEquals(
                "Root canal assessment completed",
                updatedTreatment.get()
                        .getTreatmentNotes());

        assertEquals(
                "Pain relief and antibiotics",
                updatedTreatment.get()
                        .getPrescription());
    }

    @Test
    @DisplayName(
            "Should delete an existing treatment")
    void shouldDeleteExistingTreatment()
            throws Exception {

        long generatedTreatmentId =
                treatmentDAO.save(
                        createTreatment());

        boolean deleted =
                treatmentDAO.delete(
                        generatedTreatmentId);

        Optional<Treatment> deletedTreatment =
                treatmentDAO.findById(
                        generatedTreatmentId);

        assertTrue(deleted);

        assertFalse(deletedTreatment.isPresent());
    }

    private void createDatabaseTables()
            throws SQLException {

        try (Statement statement =
                databaseKeeper.createStatement()) {

            statement.execute(
                    "DROP TABLE IF EXISTS treatments");

            statement.execute(
                    "DROP TABLE IF EXISTS appointments");

            statement.execute(
                    "DROP TABLE IF EXISTS patients");

            statement.execute(
                    "DROP TABLE IF EXISTS users");

            statement.execute("""
                    CREATE TABLE users (
                        user_id BIGINT
                            AUTO_INCREMENT
                            PRIMARY KEY,

                        username VARCHAR(50)
                            NOT NULL
                            UNIQUE,

                        password_hash VARCHAR(255)
                            NOT NULL,

                        full_name VARCHAR(100)
                            NOT NULL,

                        role VARCHAR(20)
                            NOT NULL,

                        email VARCHAR(100),

                        phone VARCHAR(20),

                        active BOOLEAN
                            NOT NULL
                            DEFAULT TRUE,

                        created_at TIMESTAMP
                            NOT NULL
                            DEFAULT CURRENT_TIMESTAMP,

                        updated_at TIMESTAMP
                            NOT NULL
                            DEFAULT CURRENT_TIMESTAMP
                    )
                    """);

            statement.execute("""
                    CREATE TABLE patients (
                        patient_id BIGINT
                            AUTO_INCREMENT
                            PRIMARY KEY,

                        patient_number VARCHAR(20)
                            NOT NULL
                            UNIQUE,

                        first_name VARCHAR(50)
                            NOT NULL,

                        last_name VARCHAR(50)
                            NOT NULL,

                        date_of_birth DATE
                            NOT NULL,

                        gender VARCHAR(10)
                            NOT NULL,

                        nic_number VARCHAR(20)
                            UNIQUE,

                        phone VARCHAR(20)
                            NOT NULL,

                        email VARCHAR(100),

                        address VARCHAR(255),

                        medical_notes CLOB,

                        active BOOLEAN
                            NOT NULL
                            DEFAULT TRUE,

                        created_at TIMESTAMP
                            NOT NULL
                            DEFAULT CURRENT_TIMESTAMP,

                        updated_at TIMESTAMP
                            NOT NULL
                            DEFAULT CURRENT_TIMESTAMP
                    )
                    """);

            statement.execute("""
                    CREATE TABLE appointments (
                        appointment_id BIGINT
                            AUTO_INCREMENT
                            PRIMARY KEY,

                        appointment_number VARCHAR(20)
                            NOT NULL
                            UNIQUE,

                        patient_id BIGINT
                            NOT NULL,

                        dentist_id BIGINT
                            NOT NULL,

                        appointment_date DATE
                            NOT NULL,

                        appointment_time TIME
                            NOT NULL,

                        reason VARCHAR(255)
                            NOT NULL,

                        status VARCHAR(20)
                            NOT NULL
                            DEFAULT 'SCHEDULED',

                        notes CLOB,

                        created_at TIMESTAMP
                            NOT NULL
                            DEFAULT CURRENT_TIMESTAMP,

                        updated_at TIMESTAMP
                            NOT NULL
                            DEFAULT CURRENT_TIMESTAMP,

                        CONSTRAINT fk_test_appointment_patient
                            FOREIGN KEY (patient_id)
                            REFERENCES patients(patient_id),

                        CONSTRAINT fk_test_appointment_dentist
                            FOREIGN KEY (dentist_id)
                            REFERENCES users(user_id)
                    )
                    """);

            statement.execute("""
                    CREATE TABLE treatments (
                        treatment_id BIGINT
                            AUTO_INCREMENT
                            PRIMARY KEY,

                        appointment_id BIGINT
                            NOT NULL,

                        dentist_id BIGINT
                            NOT NULL,

                        treatment_date DATE
                            NOT NULL,

                        diagnosis VARCHAR(500),

                        treatment_notes CLOB
                            NOT NULL,

                        prescription CLOB,

                        created_at TIMESTAMP
                            NOT NULL
                            DEFAULT CURRENT_TIMESTAMP,

                        updated_at TIMESTAMP
                            NOT NULL
                            DEFAULT CURRENT_TIMESTAMP,

                        CONSTRAINT fk_test_treatment_appointment
                            FOREIGN KEY (appointment_id)
                            REFERENCES appointments(
                                appointment_id
                            ),

                        CONSTRAINT fk_test_treatment_dentist
                            FOREIGN KEY (dentist_id)
                            REFERENCES users(user_id)
                    )
                    """);
        }
    }

    private void insertRequiredTestRecords()
            throws SQLException {

        try (Statement statement =
                databaseKeeper.createStatement()) {

            statement.executeUpdate("""
                    INSERT INTO users (
                        user_id,
                        username,
                        password_hash,
                        full_name,
                        role,
                        email,
                        phone,
                        active
                    )
                    VALUES (
                        1,
                        'dentist.test',
                        'TEST_PASSWORD',
                        'Dr. Test Dentist',
                        'DENTIST',
                        'dentist@example.com',
                        '0711111111',
                        TRUE
                    )
                    """);

            statement.executeUpdate("""
                    INSERT INTO patients (
                        patient_id,
                        patient_number,
                        first_name,
                        last_name,
                        date_of_birth,
                        gender,
                        nic_number,
                        phone,
                        email,
                        address,
                        medical_notes,
                        active
                    )
                    VALUES (
                        1,
                        'PAT-TEST-001',
                        'Nimal',
                        'Fernando',
                        '1995-06-15',
                        'MALE',
                        '951671234V',
                        '0774567890',
                        'nimal@example.com',
                        'Colombo',
                        'No known allergies',
                        TRUE
                    )
                    """);

            statement.executeUpdate("""
                    INSERT INTO appointments (
                        appointment_id,
                        appointment_number,
                        patient_id,
                        dentist_id,
                        appointment_date,
                        appointment_time,
                        reason,
                        status,
                        notes
                    )
                    VALUES (
                        1,
                        'APT-TEST-001',
                        1,
                        1,
                        '2026-08-25',
                        '10:00:00',
                        'Dental consultation',
                        'COMPLETED',
                        'Appointment completed'
                    )
                    """);
        }
    }

    private Treatment createTreatment() {

        return new Treatment(
                1L,
                1L,
                LocalDate.of(2026, 8, 25),
                "Dental cavity",
                "Dental examination and tooth filling",
                "Pain relief medication");
    }
}