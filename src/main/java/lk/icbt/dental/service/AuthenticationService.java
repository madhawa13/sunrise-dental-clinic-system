package lk.icbt.dental.service;

import java.sql.SQLException;

import lk.icbt.dental.model.User;

/**
 * Defines user authentication business operations.
 */
public interface AuthenticationService {

    /**
     * Authenticates an active user using
     * username and password.
     *
     * @param username login username
     * @param password plain-text submitted password
     * @return authenticated user
     * @throws SQLException when database access fails
     */
    User authenticate(
            String username,
            String password)
            throws SQLException;

    /**
     * Checks whether an authenticated user
     * has the required system role.
     */
    boolean hasRole(
            User user,
            String requiredRole);
}