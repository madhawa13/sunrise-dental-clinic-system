package lk.icbt.dental.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lk.icbt.dental.exception.AuthenticationException;
import lk.icbt.dental.model.User;
import lk.icbt.dental.util.PasswordUtil;

/**
 * Tests authentication-related business operations.
 */
class AuthenticationServiceImplTest {

    private static final String USERNAME =
            "reception01";

    private static final String PASSWORD =
            "Reception@123";

    private UserService userService;

    private User user;

    private AuthenticationService
            authenticationService;

    /**
     * Creates fresh Mockito objects
     * before every test.
     */
    @BeforeEach
    void setUp() {

        userService =
                mock(UserService.class);

        user =
                mock(User.class);

        authenticationService =
                new AuthenticationServiceImpl(
                        userService);
    }

    /**
     * Configures an active user with a securely
     * hashed password.
     */
    private void prepareActiveUser()
            throws SQLException {

        String passwordHash =
                PasswordUtil.hashPassword(
                        PASSWORD);

        when(userService.findByUsername(USERNAME))
                .thenReturn(Optional.of(user));

        when(user.isActive())
                .thenReturn(true);

        when(user.getPasswordHash())
                .thenReturn(passwordHash);
    }

    /**
     * Valid credentials must return
     * the matching user.
     */
    @Test
    @DisplayName(
            "Should authenticate a valid user")
    void shouldAuthenticateValidUser()
            throws SQLException {

        prepareActiveUser();

        User authenticatedUser =
                authenticationService.authenticate(
                        USERNAME,
                        PASSWORD);

        assertSame(
                user,
                authenticatedUser);

        verify(userService)
                .findByUsername(USERNAME);
    }

    /**
     * An incorrect password must be rejected.
     */
    @Test
    @DisplayName(
            "Should reject an incorrect password")
    void shouldRejectIncorrectPassword()
            throws SQLException {

        prepareActiveUser();

        assertThrows(
                AuthenticationException.class,
                () -> authenticationService
                        .authenticate(
                                USERNAME,
                                "WrongPassword"));
    }

    /**
     * An unknown username must be rejected.
     */
    @Test
    @DisplayName(
            "Should reject an unknown username")
    void shouldRejectUnknownUsername()
            throws SQLException {

        when(userService.findByUsername(
                "unknown.user"))
                .thenReturn(Optional.empty());

        assertThrows(
                AuthenticationException.class,
                () -> authenticationService
                        .authenticate(
                                "unknown.user",
                                PASSWORD));
    }

    /**
     * An inactive user account must
     * not be permitted to log in.
     */
    @Test
    @DisplayName(
            "Should reject an inactive user")
    void shouldRejectInactiveUser()
            throws SQLException {

        when(userService.findByUsername(USERNAME))
                .thenReturn(Optional.of(user));

        when(user.isActive())
                .thenReturn(false);

        assertThrows(
                AuthenticationException.class,
                () -> authenticationService
                        .authenticate(
                                USERNAME,
                                PASSWORD));

        verify(user, never())
                .getPasswordHash();
    }

    /**
     * A blank username must be rejected
     * before accessing the database.
     */
    @Test
    @DisplayName(
            "Should reject a blank username")
    void shouldRejectBlankUsername()
            throws SQLException {

        assertThrows(
                AuthenticationException.class,
                () -> authenticationService
                        .authenticate(
                                " ",
                                PASSWORD));

        verify(userService, never())
                .findByUsername(
                        org.mockito.ArgumentMatchers
                                .anyString());
    }

    /**
     * A blank password must be rejected
     * before accessing the database.
     */
    @Test
    @DisplayName(
            "Should reject a blank password")
    void shouldRejectBlankPassword()
            throws SQLException {

        assertThrows(
                AuthenticationException.class,
                () -> authenticationService
                        .authenticate(
                                USERNAME,
                                " "));

        verify(userService, never())
                .findByUsername(
                        org.mockito.ArgumentMatchers
                                .anyString());
    }

    /**
     * Role matching must ignore character case.
     */
    @Test
    @DisplayName(
            "Should confirm a matching user role")
    void shouldConfirmMatchingRole() {

        when(user.getRole())
                .thenReturn("RECEPTIONIST");

        boolean hasRole =
                authenticationService.hasRole(
                        user,
                        "receptionist");

        assertTrue(hasRole);
    }

    /**
     * A user must not be granted
     * an unrelated role.
     */
    @Test
    @DisplayName(
            "Should reject a non-matching user role")
    void shouldRejectNonMatchingRole() {

        when(user.getRole())
                .thenReturn("DENTIST");

        boolean hasRole =
                authenticationService.hasRole(
                        user,
                        "RECEPTIONIST");

        assertFalse(hasRole);
    }

    /**
     * A null user must never have
     * an authorized role.
     */
    @Test
    @DisplayName(
            "Should reject role check for null user")
    void shouldRejectNullUserRoleCheck() {

        boolean hasRole =
                authenticationService.hasRole(
                        null,
                        "DENTIST");

        assertFalse(hasRole);
    }
}