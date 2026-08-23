package lk.icbt.dental.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lk.icbt.dental.dao.TreatmentChargeDAO;
import lk.icbt.dental.model.TreatmentCharge;
import lk.icbt.dental.util.ConnectionProvider;

/**
 * Tests TreatmentChargeDAOImpl using
 * an isolated in-memory H2 database.
 */
class TreatmentChargeDAOImplTest {

    private static final String TEST_DATABASE_URL =
            "jdbc:h2:mem:treatment_charge_test;"
            + "MODE=MySQL;"
            + "DB_CLOSE_DELAY=-1;"
            + "DATABASE_TO_LOWER=TRUE";

    private static final String TEST_DATABASE_USER =
            "sa";

    private static final String TEST_DATABASE_PASSWORD =
            "";

    private Connection databaseKeeper;

    private TreatmentChargeDAO treatmentChargeDAO;

    @BeforeEach
    void setUp() throws Exception {

        databaseKeeper =
                DriverManager.getConnection(
                        TEST_DATABASE_URL,
                        TEST_DATABASE_USER,
                        TEST_DATABASE_PASSWORD);

        createTreatmentChargeTable();

        ConnectionProvider connectionProvider =
                () -> DriverManager.getConnection(
                        TEST_DATABASE_URL,
                        TEST_DATABASE_USER,
                        TEST_DATABASE_PASSWORD);

        treatmentChargeDAO =
                new TreatmentChargeDAOImpl(
                        connectionProvider);
    }

    @AfterEach
    void tearDown() throws SQLException {

        if (databaseKeeper != null
                && !databaseKeeper.isClosed()) {

            databaseKeeper.close();
        }
    }

    @Test
    @DisplayName(
            "Should save charge and return generated ID")
    void shouldSaveChargeAndReturnGeneratedId()
            throws Exception {

        TreatmentCharge charge =
                createTreatmentCharge();

        long generatedId =
                treatmentChargeDAO.save(charge);

        assertTrue(generatedId > 0);

        assertEquals(
                generatedId,
                charge.getChargeId());
    }

    @Test
    @DisplayName(
            "Should find treatment charge using ID")
    void shouldFindTreatmentChargeUsingId()
            throws Exception {

        long generatedId =
                treatmentChargeDAO.save(
                        createTreatmentCharge());

        Optional<TreatmentCharge> result =
                treatmentChargeDAO.findById(
                        generatedId);

        assertTrue(result.isPresent());

        assertEquals(
                "TR100",
                result.get().getTreatmentCode());

        assertEquals(
                new BigDecimal("4500.00"),
                result.get().getStandardCharge());
    }

    @Test
    @DisplayName(
            "Should find treatment charge using code")
    void shouldFindTreatmentChargeUsingCode()
            throws Exception {

        treatmentChargeDAO.save(
                createTreatmentCharge());

        Optional<TreatmentCharge> result =
                treatmentChargeDAO.findByCode(
                        "tr100");

        assertTrue(result.isPresent());

        assertEquals(
                "Test Dental Treatment",
                result.get().getTreatmentName());
    }

    @Test
    @DisplayName(
            "Should return all treatment charges")
    void shouldReturnAllTreatmentCharges()
            throws Exception {

        treatmentChargeDAO.save(
                createTreatmentCharge());

        TreatmentCharge secondCharge =
                new TreatmentCharge(
                        "TR101",
                        "Second Test Treatment",
                        "Second charge used by tests",
                        new BigDecimal("6500.00"));

        treatmentChargeDAO.save(secondCharge);

        List<TreatmentCharge> charges =
                treatmentChargeDAO.findAll();

        assertEquals(2, charges.size());
    }

    @Test
    @DisplayName(
            "Should return active treatment charges only")
    void shouldReturnActiveTreatmentChargesOnly()
            throws Exception {

        TreatmentCharge activeCharge =
                createTreatmentCharge();

        treatmentChargeDAO.save(activeCharge);

        TreatmentCharge inactiveCharge =
                new TreatmentCharge(
                        "TR101",
                        "Inactive Treatment",
                        "Inactive price-list item",
                        new BigDecimal("3000.00"));

        inactiveCharge.setActive(false);

        treatmentChargeDAO.save(
                inactiveCharge);

        List<TreatmentCharge> activeCharges =
                treatmentChargeDAO
                        .findAllActive();

        assertEquals(
                1,
                activeCharges.size());

        assertEquals(
                "TR100",
                activeCharges.get(0)
                        .getTreatmentCode());

        assertTrue(
                activeCharges.get(0)
                        .isActive());
    }

    @Test
    @DisplayName(
            "Should update an existing treatment charge")
    void shouldUpdateExistingTreatmentCharge()
            throws Exception {

        TreatmentCharge charge =
                createTreatmentCharge();

        long generatedId =
                treatmentChargeDAO.save(charge);

        charge.setChargeId(generatedId);

        charge.setTreatmentName(
                "Updated Dental Treatment");

        charge.setDescription(
                "Updated treatment description");

        charge.setStandardCharge(
                new BigDecimal("5000.00"));

        boolean updated =
                treatmentChargeDAO.update(
                        charge);

        Optional<TreatmentCharge> result =
                treatmentChargeDAO.findById(
                        generatedId);

        assertTrue(updated);

        assertTrue(result.isPresent());

        assertEquals(
                "Updated Dental Treatment",
                result.get().getTreatmentName());

        assertEquals(
                new BigDecimal("5000.00"),
                result.get().getStandardCharge());
    }

    @Test
    @DisplayName(
            "Should deactivate an existing treatment charge")
    void shouldDeactivateExistingTreatmentCharge()
            throws Exception {

        long generatedId =
                treatmentChargeDAO.save(
                        createTreatmentCharge());

        boolean updated =
                treatmentChargeDAO
                        .updateActiveStatus(
                                generatedId,
                                false);

        Optional<TreatmentCharge> result =
                treatmentChargeDAO.findById(
                        generatedId);

        assertTrue(updated);

        assertTrue(result.isPresent());

        assertFalse(
                result.get().isActive());

        assertTrue(
                treatmentChargeDAO
                        .findAllActive()
                        .isEmpty());
    }

    private void createTreatmentChargeTable()
            throws SQLException {

        try (Statement statement =
                databaseKeeper.createStatement()) {

            statement.execute("""
                    DROP TABLE IF EXISTS treatment_charges
                    """);

            statement.execute("""
                    CREATE TABLE treatment_charges (
                        charge_id BIGINT
                            AUTO_INCREMENT
                            PRIMARY KEY,

                        treatment_code VARCHAR(20)
                            NOT NULL
                            UNIQUE,

                        treatment_name VARCHAR(100)
                            NOT NULL,

                        description VARCHAR(255),

                        standard_charge DECIMAL(10, 2)
                            NOT NULL,

                        active BOOLEAN
                            NOT NULL
                            DEFAULT TRUE,

                        created_at TIMESTAMP
                            NOT NULL
                            DEFAULT CURRENT_TIMESTAMP,

                        CONSTRAINT chk_test_standard_charge
                            CHECK (standard_charge >= 0)
                    )
                    """);
        }
    }

    private TreatmentCharge createTreatmentCharge() {

        return new TreatmentCharge(
                "TR100",
                "Test Dental Treatment",
                "Treatment charge used by an automated test",
                new BigDecimal("4500.00"));
    }
}