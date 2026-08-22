package lk.icbt.dental.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lk.icbt.dental.dao.AppointmentDAO;
import lk.icbt.dental.model.Appointment;
import lk.icbt.dental.util.ConnectionProvider;
import lk.icbt.dental.util.DatabaseConnection;

/**
 * JDBC implementation of AppointmentDAO.
 */
public class AppointmentDAOImpl
        implements AppointmentDAO {

    private static final String BASE_SELECT_SQL = """
            SELECT
                a.appointment_id,
                a.appointment_number,
                a.patient_id,
                a.dentist_id,
                a.appointment_date,
                a.appointment_time,
                a.reason,
                a.status,
                a.notes,
                a.created_at,
                a.updated_at,
                CONCAT(
                    p.first_name,
                    ' ',
                    p.last_name
                ) AS patient_name,
                u.full_name AS dentist_name
            FROM appointments a
            INNER JOIN patients p
                ON a.patient_id = p.patient_id
            INNER JOIN users u
                ON a.dentist_id = u.user_id
            """;

    private static final String INSERT_SQL = """
            INSERT INTO appointments (
                appointment_number,
                patient_id,
                dentist_id,
                appointment_date,
                appointment_time,
                reason,
                status,
                notes
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String FIND_BY_ID_SQL =
            BASE_SELECT_SQL
            + """
              WHERE a.appointment_id = ?
              """;

    private static final String FIND_ALL_SQL =
            BASE_SELECT_SQL
            + """
              ORDER BY
                  a.appointment_date DESC,
                  a.appointment_time DESC
              """;

    private static final String FIND_BY_DATE_SQL =
            BASE_SELECT_SQL
            + """
              WHERE a.appointment_date = ?
              ORDER BY a.appointment_time ASC
              """;

    private static final String SEARCH_SQL =
            BASE_SELECT_SQL
            + """
              WHERE
                    LOWER(a.appointment_number) LIKE ?
                 OR LOWER(p.first_name) LIKE ?
                 OR LOWER(p.last_name) LIKE ?
                 OR LOWER(u.full_name) LIKE ?
                 OR LOWER(a.status) LIKE ?
              ORDER BY
                  a.appointment_date DESC,
                  a.appointment_time DESC
              """;

    private static final String CHECK_AVAILABILITY_SQL = """
            SELECT COUNT(*) AS booking_count
            FROM appointments
            WHERE dentist_id = ?
              AND appointment_date = ?
              AND appointment_time = ?
              AND status <> 'CANCELLED'
            """;

    private static final String
            CHECK_AVAILABILITY_EXCLUDING_ID_SQL = """
            SELECT COUNT(*) AS booking_count
            FROM appointments
            WHERE dentist_id = ?
              AND appointment_date = ?
              AND appointment_time = ?
              AND status <> 'CANCELLED'
              AND appointment_id <> ?
            """;

    private static final String UPDATE_SQL = """
            UPDATE appointments
            SET patient_id = ?,
                dentist_id = ?,
                appointment_date = ?,
                appointment_time = ?,
                reason = ?,
                status = ?,
                notes = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE appointment_id = ?
            """;

    private static final String UPDATE_STATUS_SQL = """
            UPDATE appointments
            SET status = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE appointment_id = ?
            """;

    private static final String CANCEL_SQL = """
            UPDATE appointments
            SET status = 'CANCELLED',
                updated_at = CURRENT_TIMESTAMP
            WHERE appointment_id = ?
              AND status <> 'CANCELLED'
            """;

    private final ConnectionProvider connectionProvider;

    /**
     * Constructor used by the real application.
     */
    public AppointmentDAOImpl() {
        this(DatabaseConnection::getConnection);
    }

    /**
     * Constructor used by H2 automated tests.
     */
    public AppointmentDAOImpl(
            ConnectionProvider connectionProvider) {

        if (connectionProvider == null) {
            throw new IllegalArgumentException(
                    "Connection provider cannot be null");
        }

        this.connectionProvider = connectionProvider;
    }

    /**
     * Saves a new appointment.
     */
    @Override
    public long save(Appointment appointment)
            throws SQLException {

        validateAppointmentObject(appointment);

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            INSERT_SQL,
                            Statement.RETURN_GENERATED_KEYS)
        ) {

            statement.setString(
                    1,
                    appointment.getAppointmentNumber());

            statement.setLong(
                    2,
                    appointment.getPatientId());

            statement.setLong(
                    3,
                    appointment.getDentistId());

            statement.setDate(
                    4,
                    Date.valueOf(
                            appointment.getAppointmentDate()));

            statement.setTime(
                    5,
                    Time.valueOf(
                            appointment.getAppointmentTime()));

            statement.setString(
                    6,
                    appointment.getReason());

            statement.setString(
                    7,
                    appointment.getStatus());

            statement.setString(
                    8,
                    appointment.getNotes());

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException(
                        "Appointment could not be saved");
            }

            try (ResultSet generatedKeys =
                    statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {

                    long generatedId =
                            generatedKeys.getLong(1);

                    appointment.setAppointmentId(
                            generatedId);

                    return generatedId;
                }
            }

            throw new SQLException(
                    "Appointment was saved, "
                    + "but no ID was generated");
        }
    }

    /**
     * Finds an appointment using its ID.
     */
    @Override
    public Optional<Appointment> findById(
            long appointmentId)
            throws SQLException {

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            FIND_BY_ID_SQL)
        ) {

            statement.setLong(
                    1,
                    appointmentId);

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(
                            mapAppointment(resultSet));
                }

                return Optional.empty();
            }
        }
    }

    /**
     * Returns all appointments.
     */
    @Override
    public List<Appointment> findAll()
            throws SQLException {

        List<Appointment> appointments =
                new ArrayList<>();

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            FIND_ALL_SQL);

            ResultSet resultSet =
                    statement.executeQuery()
        ) {

            while (resultSet.next()) {

                appointments.add(
                        mapAppointment(resultSet));
            }
        }

        return appointments;
    }

    /**
     * Returns appointments for a selected date.
     */
    @Override
    public List<Appointment> findByDate(
            LocalDate appointmentDate)
            throws SQLException {

        if (appointmentDate == null) {
            throw new IllegalArgumentException(
                    "Appointment date cannot be null");
        }

        List<Appointment> appointments =
                new ArrayList<>();

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            FIND_BY_DATE_SQL)
        ) {

            statement.setDate(
                    1,
                    Date.valueOf(appointmentDate));

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                while (resultSet.next()) {

                    appointments.add(
                            mapAppointment(resultSet));
                }
            }
        }

        return appointments;
    }

    /**
     * Searches appointments.
     */
    @Override
    public List<Appointment> search(
            String searchTerm)
            throws SQLException {

        if (searchTerm == null
                || searchTerm.isBlank()) {

            return findAll();
        }

        List<Appointment> appointments =
                new ArrayList<>();

        String searchPattern =
                "%"
                + searchTerm.trim().toLowerCase()
                + "%";

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            SEARCH_SQL)
        ) {

            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            statement.setString(3, searchPattern);
            statement.setString(4, searchPattern);
            statement.setString(5, searchPattern);

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                while (resultSet.next()) {

                    appointments.add(
                            mapAppointment(resultSet));
                }
            }
        }

        return appointments;
    }

    /**
     * Checks whether a dentist is available.
     */
    @Override
    public boolean isDentistAvailable(
            long dentistId,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            Long excludedAppointmentId)
            throws SQLException {

        if (dentistId <= 0) {
            throw new IllegalArgumentException(
                    "Dentist ID must be positive");
        }

        if (appointmentDate == null) {
            throw new IllegalArgumentException(
                    "Appointment date is required");
        }

        if (appointmentTime == null) {
            throw new IllegalArgumentException(
                    "Appointment time is required");
        }

        boolean excludeCurrentAppointment =
                excludedAppointmentId != null;

        String availabilitySql =
                excludeCurrentAppointment
                        ? CHECK_AVAILABILITY_EXCLUDING_ID_SQL
                        : CHECK_AVAILABILITY_SQL;

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            availabilitySql)
        ) {

            statement.setLong(
                    1,
                    dentistId);

            statement.setDate(
                    2,
                    Date.valueOf(appointmentDate));

            statement.setTime(
                    3,
                    Time.valueOf(appointmentTime));

            if (excludeCurrentAppointment) {

                statement.setLong(
                        4,
                        excludedAppointmentId);
            }

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                if (resultSet.next()) {

                    int bookingCount =
                            resultSet.getInt(
                                    "booking_count");

                    return bookingCount == 0;
                }

                return false;
            }
        }
    }

    /**
     * Updates an existing appointment.
     */
    @Override
    public boolean update(
            Appointment appointment)
            throws SQLException {

        validateAppointmentObject(appointment);

        if (appointment.getAppointmentId() == null
                || appointment.getAppointmentId() <= 0) {

            throw new IllegalArgumentException(
                    "Appointment ID is required");
        }

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            UPDATE_SQL)
        ) {

            statement.setLong(
                    1,
                    appointment.getPatientId());

            statement.setLong(
                    2,
                    appointment.getDentistId());

            statement.setDate(
                    3,
                    Date.valueOf(
                            appointment.getAppointmentDate()));

            statement.setTime(
                    4,
                    Time.valueOf(
                            appointment.getAppointmentTime()));

            statement.setString(
                    5,
                    appointment.getReason());

            statement.setString(
                    6,
                    appointment.getStatus());

            statement.setString(
                    7,
                    appointment.getNotes());

            statement.setLong(
                    8,
                    appointment.getAppointmentId());

            return statement.executeUpdate() == 1;
        }
    }

    /**
     * Updates appointment status.
     */
    @Override
    public boolean updateStatus(
            long appointmentId,
            String status)
            throws SQLException {

        if (appointmentId <= 0) {
            throw new IllegalArgumentException(
                    "Appointment ID must be positive");
        }

        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException(
                    "Appointment status is required");
        }

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            UPDATE_STATUS_SQL)
        ) {

            statement.setString(
                    1,
                    status);

            statement.setLong(
                    2,
                    appointmentId);

            return statement.executeUpdate() == 1;
        }
    }

    /**
     * Cancels an appointment.
     */
    @Override
    public boolean cancel(long appointmentId)
            throws SQLException {

        if (appointmentId <= 0) {
            throw new IllegalArgumentException(
                    "Appointment ID must be positive");
        }

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            CANCEL_SQL)
        ) {

            statement.setLong(
                    1,
                    appointmentId);

            return statement.executeUpdate() == 1;
        }
    }

    /**
     * Converts a ResultSet row into an Appointment.
     */
    private Appointment mapAppointment(
            ResultSet resultSet)
            throws SQLException {

        Appointment appointment =
                new Appointment();

        appointment.setAppointmentId(
                resultSet.getLong(
                        "appointment_id"));

        appointment.setAppointmentNumber(
                resultSet.getString(
                        "appointment_number"));

        appointment.setPatientId(
                resultSet.getLong(
                        "patient_id"));

        appointment.setDentistId(
                resultSet.getLong(
                        "dentist_id"));

        Date appointmentDate =
                resultSet.getDate(
                        "appointment_date");

        if (appointmentDate != null) {

            appointment.setAppointmentDate(
                    appointmentDate.toLocalDate());
        }

        Time appointmentTime =
                resultSet.getTime(
                        "appointment_time");

        if (appointmentTime != null) {

            appointment.setAppointmentTime(
                    appointmentTime.toLocalTime());
        }

        appointment.setReason(
                resultSet.getString("reason"));

        appointment.setStatus(
                resultSet.getString("status"));

        appointment.setNotes(
                resultSet.getString("notes"));

        appointment.setPatientName(
                resultSet.getString(
                        "patient_name"));

        appointment.setDentistName(
                resultSet.getString(
                        "dentist_name"));

        Timestamp createdTimestamp =
                resultSet.getTimestamp(
                        "created_at");

        if (createdTimestamp != null) {

            appointment.setCreatedAt(
                    createdTimestamp.toLocalDateTime());
        }

        Timestamp updatedTimestamp =
                resultSet.getTimestamp(
                        "updated_at");

        if (updatedTimestamp != null) {

            appointment.setUpdatedAt(
                    updatedTimestamp.toLocalDateTime());
        }

        return appointment;
    }

    /**
     * Performs basic validation before insert/update.
     */
    private void validateAppointmentObject(
            Appointment appointment) {

        if (appointment == null) {
            throw new IllegalArgumentException(
                    "Appointment cannot be null");
        }

        if (appointment.getAppointmentNumber() == null
                || appointment.getAppointmentNumber()
                        .isBlank()) {

            throw new IllegalArgumentException(
                    "Appointment number is required");
        }

        if (appointment.getPatientId() == null
                || appointment.getPatientId() <= 0) {

            throw new IllegalArgumentException(
                    "Patient ID is required");
        }

        if (appointment.getDentistId() == null
                || appointment.getDentistId() <= 0) {

            throw new IllegalArgumentException(
                    "Dentist ID is required");
        }

        if (appointment.getAppointmentDate() == null) {

            throw new IllegalArgumentException(
                    "Appointment date is required");
        }

        if (appointment.getAppointmentTime() == null) {

            throw new IllegalArgumentException(
                    "Appointment time is required");
        }

        if (appointment.getReason() == null
                || appointment.getReason().isBlank()) {

            throw new IllegalArgumentException(
                    "Appointment reason is required");
        }

        if (appointment.getStatus() == null
                || appointment.getStatus().isBlank()) {

            appointment.setStatus(
                    Appointment.STATUS_SCHEDULED);
        }
    }
}