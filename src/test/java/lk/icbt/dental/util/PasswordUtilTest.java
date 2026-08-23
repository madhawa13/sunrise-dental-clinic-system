package lk.icbt.dental.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests secure password hashing and verification.
 */
class PasswordUtilTest {

    /**
     * Confirms that a password is converted
     * into a secure hash.
     */
    @Test
    @DisplayName(
            "Should securely hash a password")
    void shouldSecurelyHashPassword() {

        String plainPassword =
                "Reception@123";

        String passwordHash =
                PasswordUtil.hashPassword(
                        plainPassword);

        assertNotNull(passwordHash);

        assertFalse(
                passwordHash.isBlank());

        assertNotEquals(
                plainPassword,
                passwordHash);

        assertTrue(
                passwordHash.split(":").length == 3);
    }

    /**
     * Confirms that the correct password
     * matches its stored hash.
     */
    @Test
    @DisplayName(
            "Should verify the correct password")
    void shouldVerifyCorrectPassword() {

        String plainPassword =
                "Reception@123";

        String passwordHash =
                PasswordUtil.hashPassword(
                        plainPassword);

        boolean passwordMatches =
                PasswordUtil.verifyPassword(
                        plainPassword,
                        passwordHash);

        assertTrue(passwordMatches);
    }

    /**
     * Confirms that an incorrect password
     * does not match the stored hash.
     */
    @Test
    @DisplayName(
            "Should reject an incorrect password")
    void shouldRejectIncorrectPassword() {

        String passwordHash =
                PasswordUtil.hashPassword(
                        "Reception@123");

        boolean passwordMatches =
                PasswordUtil.verifyPassword(
                        "WrongPassword",
                        passwordHash);

        assertFalse(passwordMatches);
    }

    /**
     * Confirms that empty passwords cannot
     * be converted into password hashes.
     */
    @Test
    @DisplayName(
            "Should reject an empty password")
    void shouldRejectEmptyPassword() {

        assertThrows(
                IllegalArgumentException.class,
                () -> PasswordUtil.hashPassword(""));
    }

    /**
     * Confirms that an invalid stored hash
     * is safely rejected.
     */
    @Test
    @DisplayName(
            "Should reject an invalid stored hash")
    void shouldRejectInvalidStoredHash() {

        boolean passwordMatches =
                PasswordUtil.verifyPassword(
                        "Reception@123",
                        "invalid-hash");

        assertFalse(passwordMatches);
    }
}