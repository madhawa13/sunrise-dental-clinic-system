package lk.icbt.dental.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lk.icbt.dental.dao.AppointmentDAO;
import lk.icbt.dental.model.Appointment;

class AppointmentDAOImplTest {

    private static final String TEST_DATABASE_URL =
            "jdbc:h2:mem:appointment_test;"
            + "MODE=MySQL;"
            + "DB_CLOSE_DELAY=-1";

    private Connection databaseKeeper;
    private AppointmentDAO appointmentDAO;

    @BeforeEach
    void setUp() throws Exception {

        databaseKeeper = DriverManager.getConnection(
                TEST_DATABASE_URL);

        createTestTables(databaseKeeper);
        insertReferenceData(databaseKeeper);

        appointmentDAO = new AppointmentDAOImpl(
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
                        "DROP TABLE IF EXISTS appointments");

                statement.execute(
                        "DROP TABLE IF EXISTS patients");

                statement.execute(
                        "DROP TABLE IF EXISTS users");
            }

            databaseKeeper.close();
        }
    }

    @Test
    @DisplayName(
            "Should save and find an appointment by ID")
    void shouldSaveAndFindAppointment()
            throws Exception {

        Appointment appointment =
                createAppointment();

        long generatedId =
                appointmentDAO.save(appointment);

        assertTrue(generatedId > 0);

        assertEquals(
                generatedId,
                appointment.getAppointmentId());

        Appointment savedAppointment =
                appointmentDAO.findById(generatedId)
                        .orElseThrow();

        assertEquals(
                "APT-TEST-001",
                savedAppointment.getAppointmentNumber());

        assertEquals(
                "Nimal Perera",
                savedAppointment.getPatientName());

        assertEquals(
                "Dr. Amara Silva",
                savedAppointment.getDentistName());

        assertEquals(
                Appointment.STATUS_SCHEDULED,
                savedAppointment.getStatus());
    }

    @Test
    @DisplayName("Should return all appointments")
    void shouldReturnAllAppointments()
            throws Exception {

        appointmentDAO.save(
                createAppointment());

        Appointment secondAppointment =
                createAppointment();

        secondAppointment.setAppointmentNumber(
                "APT-TEST-002");

        secondAppointment.setAppointmentTime(
                LocalTime.of(11, 30));

        appointmentDAO.save(
                secondAppointment);

        List<Appointment> appointments =
                appointmentDAO.findAll();

        assertEquals(
                2,
                appointments.size());
    }

    @Test
    @DisplayName(
            "Should return appointments for selected date")
    void shouldReturnAppointmentsForDate()
            throws Exception {

        appointmentDAO.save(
                createAppointment());

        List<Appointment> appointments =
                appointmentDAO.findByDate(
                        LocalDate.of(2030, 6, 15));

        assertEquals(
                1,
                appointments.size());

        assertEquals(
                LocalDate.of(2030, 6, 15),
                appointments.get(0)
                        .getAppointmentDate());
    }

    @Test
    @DisplayName(
            "Should search using appointment number")
    void shouldSearchUsingAppointmentNumber()
            throws Exception {

        appointmentDAO.save(
                createAppointment());

        List<Appointment> appointments =
                appointmentDAO.search(
                        "APT-TEST-001");

        assertEquals(
                1,
                appointments.size());

        assertEquals(
                "APT-TEST-001",
                appointments.get(0)
                        .getAppointmentNumber());
    }

    @Test
    @DisplayName(
            "Should prevent dentist double booking")
    void shouldCheckDentistAvailability()
            throws Exception {

        LocalDate appointmentDate =
                LocalDate.of(2030, 6, 15);

        LocalTime appointmentTime =
                LocalTime.of(10, 30);

        boolean initiallyAvailable =
                appointmentDAO.isDentistAvailable(
                        1L,
                        appointmentDate,
                        appointmentTime,
                        null);

        assertTrue(initiallyAvailable);

        Appointment appointment =
                createAppointment();

        long appointmentId =
                appointmentDAO.save(appointment);

        boolean availableAfterBooking =
                appointmentDAO.isDentistAvailable(
                        1L,
                        appointmentDate,
                        appointmentTime,
                        null);

        assertFalse(availableAfterBooking);

        boolean availableWhenExcluded =
                appointmentDAO.isDentistAvailable(
                        1L,
                        appointmentDate,
                        appointmentTime,
                        appointmentId);

        assertTrue(availableWhenExcluded);
    }

    @Test
    @DisplayName(
            "Should update an existing appointment")
    void shouldUpdateAppointment()
            throws Exception {

        Appointment appointment =
                createAppointment();

        long appointmentId =
                appointmentDAO.save(appointment);

        appointment.setAppointmentId(
                appointmentId);

        appointment.setAppointmentTime(
                LocalTime.of(14, 0));

        appointment.setReason(
                "Severe tooth pain");

        appointment.setNotes(
                "Afternoon appointment requested");

        boolean updated =
                appointmentDAO.update(
                        appointment);

        assertTrue(updated);

        Appointment updatedAppointment =
                appointmentDAO.findById(
                        appointmentId)
                        .orElseThrow();

        assertEquals(
                LocalTime.of(14, 0),
                updatedAppointment.getAppointmentTime());

        assertEquals(
                "Severe tooth pain",
                updatedAppointment.getReason());
    }

    @Test
    @DisplayName(
            "Should cancel a scheduled appointment")
    void shouldCancelAppointment()
            throws Exception {

        Appointment appointment =
                createAppointment();

        long appointmentId =
                appointmentDAO.save(appointment);

        boolean cancelled =
                appointmentDAO.cancel(
                        appointmentId);

        assertTrue(cancelled);

        Appointment cancelledAppointment =
                appointmentDAO.findById(
                        appointmentId)
                        .orElseThrow();

        assertEquals(
                Appointment.STATUS_CANCELLED,
                cancelledAppointment.getStatus());
    }

    private Appointment createAppointment() {

        return new Appointment(
                "APT-TEST-001",
                1L,
                1L,
                LocalDate.of(2030, 6, 15),
                LocalTime.of(10, 30),
                "Dental consultation",
                "First dental appointment");
    }

    private void createTestTables(
            Connection connection)
            throws Exception {

        String createUsersTable = """
                CREATE TABLE users (
                    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    username VARCHAR(50) NOT NULL UNIQUE,
                    password_hash VARCHAR(255) NOT NULL,
                    full_name VARCHAR(100) NOT NULL,
                    role VARCHAR(20) NOT NULL,
                    active BOOLEAN NOT NULL DEFAULT TRUE
                )
                """;

        String createPatientsTable = """
                CREATE TABLE patients (
                    patient_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    patient_number VARCHAR(20) NOT NULL UNIQUE,
                    first_name VARCHAR(50) NOT NULL,
                    last_name VARCHAR(50) NOT NULL,
                    date_of_birth DATE NOT NULL,
                    gender VARCHAR(10) NOT NULL,
                    phone VARCHAR(20) NOT NULL,
                    active BOOLEAN NOT NULL DEFAULT TRUE
                )
                """;

        String createAppointmentsTable = """
                CREATE TABLE appointments (
                    appointment_id BIGINT
                        PRIMARY KEY AUTO_INCREMENT,
                    appointment_number VARCHAR(20)
                        NOT NULL UNIQUE,
                    patient_id BIGINT NOT NULL,
                    dentist_id BIGINT NOT NULL,
                    appointment_date DATE NOT NULL,
                    appointment_time TIME NOT NULL,
                    reason VARCHAR(255) NOT NULL,
                    status VARCHAR(20) NOT NULL
                        DEFAULT 'SCHEDULED',
                    notes CLOB,
                    created_at TIMESTAMP NOT NULL
                        DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP NOT NULL
                        DEFAULT CURRENT_TIMESTAMP,

                    CONSTRAINT fk_test_appointment_patient
                        FOREIGN KEY (patient_id)
                        REFERENCES patients(patient_id),

                    CONSTRAINT fk_test_appointment_dentist
                        FOREIGN KEY (dentist_id)
                        REFERENCES users(user_id),

                    CONSTRAINT uq_test_dentist_schedule
                        UNIQUE (
                            dentist_id,
                            appointment_date,
                            appointment_time
                        )
                )
                """;

        try (Statement statement =
                connection.createStatement()) {

            statement.execute(createUsersTable);
            statement.execute(createPatientsTable);
            statement.execute(createAppointmentsTable);
        }
    }

    private void insertReferenceData(
            Connection connection)
            throws Exception {

        String insertDentist = """
                INSERT INTO users (
                    user_id,
                    username,
                    password_hash,
                    full_name,
                    role,
                    active
                )
                VALUES (
                    1,
                    'amara',
                    'test-password',
                    'Dr. Amara Silva',
                    'DENTIST',
                    TRUE
                )
                """;

        String insertPatient = """
                INSERT INTO patients (
                    patient_id,
                    patient_number,
                    first_name,
                    last_name,
                    date_of_birth,
                    gender,
                    phone,
                    active
                )
                VALUES (
                    1,
                    'PAT-TEST-001',
                    'Nimal',
                    'Perera',
                    '1990-05-15',
                    'MALE',
                    '0771234567',
                    TRUE
                )
                """;

        try (Statement statement =
                connection.createStatement()) {

            statement.executeUpdate(insertDentist);
            statement.executeUpdate(insertPatient);
        }
    }
}