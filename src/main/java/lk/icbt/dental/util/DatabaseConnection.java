package lk.icbt.dental.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Creates database connections for the Sunrise Dental Clinic system.
 *
 * The database configuration is loaded from:
 * src/main/resources/db.properties
 */
public final class DatabaseConnection {

    private static final String PROPERTIES_FILE = "db.properties";

    private static final Properties PROPERTIES = new Properties();

    private static final String DATABASE_URL;
    private static final String DATABASE_USERNAME;
    private static final String DATABASE_PASSWORD;

    static {
        try (InputStream inputStream =
                DatabaseConnection.class
                        .getClassLoader()
                        .getResourceAsStream(PROPERTIES_FILE)) {

            if (inputStream == null) {
                throw new IllegalStateException(
                        "Database configuration file was not found: "
                                + PROPERTIES_FILE);
            }

            PROPERTIES.load(inputStream);

            String driver = getRequiredProperty("db.driver");

            DATABASE_URL = getRequiredProperty("db.url");
            DATABASE_USERNAME = getRequiredProperty("db.username");
            DATABASE_PASSWORD = PROPERTIES.getProperty(
                    "db.password",
                    "");

            Class.forName(driver);

        } catch (IOException exception) {
            throw new ExceptionInInitializerError(
                    "Unable to read database configuration: "
                            + exception.getMessage());

        } catch (ClassNotFoundException exception) {
            throw new ExceptionInInitializerError(
                    "MySQL JDBC driver was not found: "
                            + exception.getMessage());
        }
    }

    private DatabaseConnection() {
        // Prevent object creation.
    }

    /**
     * Returns a new connection to the MySQL database.
     *
     * @return an open SQL connection
     * @throws SQLException when the connection cannot be created
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DATABASE_URL,
                DATABASE_USERNAME,
                DATABASE_PASSWORD);
    }

    /**
     * Reads and validates a required database property.
     *
     * @param propertyName property name
     * @return configured property value
     */
    private static String getRequiredProperty(String propertyName) {
        String value = PROPERTIES.getProperty(propertyName);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Required database property is missing: "
                            + propertyName);
        }

        return value.trim();
    }
}