package lk.icbt.dental.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Provides secure password hashing and verification
 * using PBKDF2 with HMAC-SHA256.
 */
public final class PasswordUtil {

    private static final String ALGORITHM =
            "PBKDF2WithHmacSHA256";

    private static final int ITERATIONS =
            120_000;

    private static final int KEY_LENGTH =
            256;

    private static final int SALT_LENGTH =
            16;

    private static final String SEPARATOR =
            ":";

    /**
     * Prevents this utility class from being instantiated.
     */
    private PasswordUtil() {
    }

    /**
     * Creates a secure password hash.
     *
     * Stored format:
     * iterations:salt:hash
     *
     * @param plainPassword password entered by the user
     * @return securely formatted password hash
     */
    public static String hashPassword(
            String plainPassword) {

        validatePassword(plainPassword);

        byte[] salt =
                new byte[SALT_LENGTH];

        SecureRandom secureRandom =
                new SecureRandom();

        secureRandom.nextBytes(salt);

        byte[] passwordHash =
                generateHash(
                        plainPassword.toCharArray(),
                        salt,
                        ITERATIONS,
                        KEY_LENGTH);

        return ITERATIONS
                + SEPARATOR
                + Base64.getEncoder()
                        .encodeToString(salt)
                + SEPARATOR
                + Base64.getEncoder()
                        .encodeToString(passwordHash);
    }

    /**
     * Verifies a plain password against a stored hash.
     *
     * @param plainPassword password entered during login
     * @param storedPasswordHash hash stored in the database
     * @return true when the password is correct
     */
    public static boolean verifyPassword(
            String plainPassword,
            String storedPasswordHash) {

        if (plainPassword == null
                || plainPassword.isBlank()
                || storedPasswordHash == null
                || storedPasswordHash.isBlank()) {

            return false;
        }

        try {
            String[] components =
                    storedPasswordHash.split(
                            SEPARATOR);

            if (components.length != 3) {
                return false;
            }

            int iterations =
                    Integer.parseInt(
                            components[0]);

            if (iterations <= 0) {
                return false;
            }

            byte[] salt =
                    Base64.getDecoder()
                            .decode(components[1]);

            byte[] expectedHash =
                    Base64.getDecoder()
                            .decode(components[2]);

            byte[] actualHash =
                    generateHash(
                            plainPassword.toCharArray(),
                            salt,
                            iterations,
                            expectedHash.length * 8);

            return constantTimeEquals(
                    expectedHash,
                    actualHash);

        } catch (
                IllegalArgumentException
                | ArrayIndexOutOfBoundsException exception) {

            return false;
        }
    }

    /**
     * Generates a PBKDF2 password hash.
     */
    private static byte[] generateHash(
            char[] password,
            byte[] salt,
            int iterations,
            int keyLength) {

        PBEKeySpec keySpecification =
                new PBEKeySpec(
                        password,
                        salt,
                        iterations,
                        keyLength);

        try {
            SecretKeyFactory keyFactory =
                    SecretKeyFactory.getInstance(
                            ALGORITHM);

            return keyFactory
                    .generateSecret(keySpecification)
                    .getEncoded();

        } catch (
                NoSuchAlgorithmException
                | InvalidKeySpecException exception) {

            throw new IllegalStateException(
                    "Password hashing could not be completed",
                    exception);

        } finally {
            keySpecification.clearPassword();
        }
    }

    /**
     * Compares byte arrays without returning early.
     * This reduces timing-attack risk.
     */
    private static boolean constantTimeEquals(
            byte[] expected,
            byte[] actual) {

        if (expected == null
                || actual == null
                || expected.length != actual.length) {

            return false;
        }

        int difference = 0;

        for (int index = 0;
                index < expected.length;
                index++) {

            difference |=
                    expected[index]
                    ^ actual[index];
        }

        return difference == 0;
    }

    /**
     * Checks whether a password was supplied.
     */
    private static void validatePassword(
            String plainPassword) {

        if (plainPassword == null
                || plainPassword.isBlank()) {

            throw new IllegalArgumentException(
                    "Password cannot be empty");
        }
    }
}