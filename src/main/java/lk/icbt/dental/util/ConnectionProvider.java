package lk.icbt.dental.util;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Provides database connections to DAO classes.
 *
 * This interface allows production code to use MySQL
 * and automated tests to use an H2 test database.
 */
@FunctionalInterface
public interface ConnectionProvider {

    /**
     * Returns an open database connection.
     *
     * @return database connection
     * @throws SQLException when a connection cannot be created
     */
    Connection getConnection() throws SQLException;
}