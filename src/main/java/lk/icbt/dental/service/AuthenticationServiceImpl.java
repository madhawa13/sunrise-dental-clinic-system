package lk.icbt.dental.service;

import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;

import lk.icbt.dental.exception.AuthenticationException;
import lk.icbt.dental.model.User;
import lk.icbt.dental.util.PasswordUtil;

/**
 * Implements secure user authentication
 * and role verification.
 */
public class AuthenticationServiceImpl
        implements AuthenticationService {

    private static final String
            INVALID_LOGIN_MESSAGE =
            "Invalid username or password";

    private final UserService userService;

    /**
     * Constructor used by the real application.
     */
    public AuthenticationServiceImpl() {

        this(new UserServiceImpl());
    }

    /**
     * Constructor used by automated tests.
     *
     * @param userService user business service
     */
    public AuthenticationServiceImpl(
            UserService userService) {

        if (userService == null) {

            throw new IllegalArgumentException(
                    "User service cannot be null");
        }

        this.userService = userService;
    }

    /**
     * Authenticates an active user using a username
     * and plain-text password.
     *
     * @param username login username
     * @param password plain-text password
     * @return authenticated user
     * @throws SQLException when a database operation fails
     */
    @Override
    public User authenticate(
            String username,
            String password)
            throws SQLException {

        validateCredentials(
                username,
                password);

        String normalizedUsername =
                username.trim();

        Optional<User> optionalUser =
                userService.findByUsername(
                        normalizedUsername);

        if (optionalUser.isEmpty()) {

            throw new AuthenticationException(
                    INVALID_LOGIN_MESSAGE);
        }

        User user =
                optionalUser.get();

        if (!user.isActive()) {

            throw new AuthenticationException(
                    "This user account is inactive");
        }

        boolean passwordMatches =
                PasswordUtil.verifyPassword(
                        password,
                        user.getPasswordHash());

        if (!passwordMatches) {

            throw new AuthenticationException(
                    INVALID_LOGIN_MESSAGE);
        }

        return user;
    }

    /**
     * Checks whether an authenticated user
     * has the required role.
     *
     * @param user authenticated user
     * @param requiredRole required system role
     * @return true when the role matches
     */
    @Override
    public boolean hasRole(
            User user,
            String requiredRole) {

        if (user == null
                || requiredRole == null
                || requiredRole.isBlank()
                || user.getRole() == null
                || user.getRole().isBlank()) {

            return false;
        }

        String normalizedUserRole =
                user.getRole()
                        .trim()
                        .toUpperCase(Locale.ROOT);

        String normalizedRequiredRole =
                requiredRole
                        .trim()
                        .toUpperCase(Locale.ROOT);

        return normalizedUserRole.equals(
                normalizedRequiredRole);
    }

    /**
     * Checks whether username and password
     * values were supplied.
     */
    private void validateCredentials(
            String username,
            String password) {

        if (username == null
                || username.isBlank()) {

            throw new AuthenticationException(
                    "Username is required");
        }

        if (password == null
                || password.isBlank()) {

            throw new AuthenticationException(
                    "Password is required");
        }
    }
}