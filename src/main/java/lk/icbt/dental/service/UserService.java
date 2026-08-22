package lk.icbt.dental.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import lk.icbt.dental.model.User;

/**
 * Defines business operations related to system users.
 */
public interface UserService {

    /**
     * Returns all active dentists.
     *
     * Used by the appointment form.
     *
     * @return active dentist list
     * @throws SQLException when a database operation fails
     */
    List<User> getActiveDentists()
            throws SQLException;

    /**
     * Finds an active user by username.
     *
     * Used later by the login module.
     *
     * @param username login username
     * @return matching user when found
     * @throws SQLException when a database operation fails
     */
    Optional<User> findByUsername(
            String username)
            throws SQLException;

    /**
     * Finds an active user by database ID.
     *
     * @param userId database user ID
     * @return matching user when found
     * @throws SQLException when a database operation fails
     */
    Optional<User> findById(long userId)
            throws SQLException;
}