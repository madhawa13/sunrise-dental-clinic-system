package lk.icbt.dental.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DatabaseConnectionTest {

    @Test
    @DisplayName("Should connect successfully to the dental clinic database")
    void shouldConnectSuccessfullyToDatabase() throws Exception {

        try (Connection connection =
                DatabaseConnection.getConnection()) {

            assertNotNull(
                    connection,
                    "Database connection should not be null");

            assertTrue(
                    connection.isValid(2),
                    "Database connection should be valid");
        }
    }
}