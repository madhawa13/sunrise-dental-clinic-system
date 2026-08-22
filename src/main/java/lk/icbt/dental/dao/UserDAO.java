package lk.icbt.dental.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import lk.icbt.dental.model.User;

/**
 * Defines database operations related to system users.
 */
public interface UserDAO {

    /**
     * Finds a user using the database ID.
     *
     * @param userId database user ID
     * @return user when found
     * @throws SQLException when the database operation fails
     */
    Optional<User> findById(long userId)
            throws SQLException;

    /**
     * Finds an active user using the username.
     *
     * This method will later be used by login.
     *
     * @param username login username
     * @return user when found
     * @throws SQLException when the database operation fails
     */
    Optional<User> findByUsername(String username)
            throws SQLException;

    /**
     * Returns all active dentists.
     *
     * This method supplies the dentist dropdown
     * in the appointment form.
     *
     * @return active dentist list
     * @throws SQLException when the database operation fails
     */
    List<User> findActiveDentists()
            throws SQLException;
}