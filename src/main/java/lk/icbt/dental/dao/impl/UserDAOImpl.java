package lk.icbt.dental.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lk.icbt.dental.dao.UserDAO;
import lk.icbt.dental.model.User;
import lk.icbt.dental.util.ConnectionProvider;
import lk.icbt.dental.util.DatabaseConnection;

/**
 * JDBC implementation of UserDAO.
 */
public class UserDAOImpl implements UserDAO {

    private static final String FIND_BY_ID_SQL = """
            SELECT
                user_id,
                username,
                password_hash,
                full_name,
                role,
                email,
                phone,
                active,
                created_at,
                updated_at
            FROM users
            WHERE user_id = ?
              AND active = TRUE
            """;

    private static final String FIND_BY_USERNAME_SQL = """
            SELECT
                user_id,
                username,
                password_hash,
                full_name,
                role,
                email,
                phone,
                active,
                created_at,
                updated_at
            FROM users
            WHERE LOWER(username) = LOWER(?)
              AND active = TRUE
            """;

    private static final String
            FIND_ACTIVE_DENTISTS_SQL = """
            SELECT
                user_id,
                username,
                password_hash,
                full_name,
                role,
                email,
                phone,
                active,
                created_at,
                updated_at
            FROM users
            WHERE role = 'DENTIST'
              AND active = TRUE
            ORDER BY full_name ASC
            """;

    private final ConnectionProvider connectionProvider;

    /**
     * Constructor used by the real application.
     */
    public UserDAOImpl() {
        this(DatabaseConnection::getConnection);
    }

    /**
     * Constructor used by automated tests.
     */
    public UserDAOImpl(
            ConnectionProvider connectionProvider) {

        if (connectionProvider == null) {
            throw new IllegalArgumentException(
                    "Connection provider cannot be null");
        }

        this.connectionProvider = connectionProvider;
    }

    /**
     * Finds an active user using the database ID.
     */
    @Override
    public Optional<User> findById(long userId)
            throws SQLException {

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            FIND_BY_ID_SQL)
        ) {

            statement.setLong(1, userId);

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(
                            mapUser(resultSet));
                }

                return Optional.empty();
            }
        }
    }

    /**
     * Finds an active user using the username.
     */
    @Override
    public Optional<User> findByUsername(
            String username)
            throws SQLException {

        if (username == null
                || username.isBlank()) {

            return Optional.empty();
        }

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            FIND_BY_USERNAME_SQL)
        ) {

            statement.setString(
                    1,
                    username.trim());

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(
                            mapUser(resultSet));
                }

                return Optional.empty();
            }
        }
    }

    /**
     * Returns all active dentists.
     */
    @Override
    public List<User> findActiveDentists()
            throws SQLException {

        List<User> dentists =
                new ArrayList<>();

        try (
            Connection connection =
                    connectionProvider.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(
                            FIND_ACTIVE_DENTISTS_SQL);

            ResultSet resultSet =
                    statement.executeQuery()
        ) {

            while (resultSet.next()) {
                dentists.add(
                        mapUser(resultSet));
            }
        }

        return dentists;
    }

    /**
     * Converts a ResultSet row into a User object.
     */
    private User mapUser(ResultSet resultSet)
            throws SQLException {

        User user = new User();

        user.setUserId(
                resultSet.getLong("user_id"));

        user.setUsername(
                resultSet.getString("username"));

        user.setPasswordHash(
                resultSet.getString("password_hash"));

        user.setFullName(
                resultSet.getString("full_name"));

        user.setRole(
                resultSet.getString("role"));

        user.setEmail(
                resultSet.getString("email"));

        user.setPhone(
                resultSet.getString("phone"));

        user.setActive(
                resultSet.getBoolean("active"));

        Timestamp createdTimestamp =
                resultSet.getTimestamp("created_at");

        if (createdTimestamp != null) {
            user.setCreatedAt(
                    createdTimestamp.toLocalDateTime());
        }

        Timestamp updatedTimestamp =
                resultSet.getTimestamp("updated_at");

        if (updatedTimestamp != null) {
            user.setUpdatedAt(
                    updatedTimestamp.toLocalDateTime());
        }

        return user;
    }
}