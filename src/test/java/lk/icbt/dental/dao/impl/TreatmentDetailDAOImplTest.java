package lk.icbt.dental.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lk.icbt.dental.dao.TreatmentDetailDAO;
import lk.icbt.dental.model.TreatmentDetail;

/**
 * H2 integration tests for TreatmentDetailDAOImpl.
 */
class TreatmentDetailDAOImplTest {

    private static final String TEST_DATABASE_URL =
            "jdbc:h2:mem:treatment_detail_test;"
            + "MODE=MySQL;"
            + "DB_CLOSE_DELAY=-1";

    private TreatmentDetailDAO treatmentDetailDAO;

    /**
     * Creates a fresh H2 table before each test.
     */
    @BeforeEach
    void setUp() throws SQLException {

        try (
            Connection connection =
                    DriverManager.getConnection(
                            TEST_DATABASE_URL);

            Statement statement =
                    connection.createStatement()
        ) {

            statement.execute(
                    "DROP TABLE IF EXISTS "
                    + "treatment_details");

            statement.execute("""
                    CREATE TABLE treatment_details (
                        treatment_detail_id BIGINT
                            PRIMARY KEY AUTO_INCREMENT,
                        treatment_id BIGINT NOT NULL,
                        charge_id BIGINT NOT NULL,
                        quantity INT NOT NULL DEFAULT 1,
                        unit_price DECIMAL(10,2) NOT NULL,
                        notes VARCHAR(255)
                    )
                    """);
        }

        treatmentDetailDAO =
                new TreatmentDetailDAOImpl(
                        () -> DriverManager.getConnection(
                                TEST_DATABASE_URL));
    }

    /**
     * Tests saving and generated ID retrieval.
     */
    @Test
    @DisplayName(
            "Should save treatment detail and return generated ID")
    void shouldSaveTreatmentDetailAndReturnGeneratedId()
            throws SQLException {

        TreatmentDetail treatmentDetail =
                createTreatmentDetail(
                        1L,
                        2L,
                        2,
                        "3500.00",
                        "Dental cleaning charge");

        long generatedId =
                treatmentDetailDAO.save(
                        treatmentDetail);

        assertTrue(generatedId > 0);

        assertEquals(
                Long.valueOf(generatedId),
                treatmentDetail.getTreatmentDetailId());

        assertNotNull(
                treatmentDetailDAO
                        .findById(generatedId)
                        .orElse(null));
    }

    /**
     * Tests finding a treatment detail by ID.
     */
    @Test
    @DisplayName(
            "Should find treatment detail using ID")
    void shouldFindTreatmentDetailUsingId()
            throws SQLException {

        TreatmentDetail treatmentDetail =
                createTreatmentDetail(
                        1L,
                        3L,
                        1,
                        "5000.00",
                        "Tooth filling charge");

        long generatedId =
                treatmentDetailDAO.save(
                        treatmentDetail);

        Optional<TreatmentDetail> result =
                treatmentDetailDAO.findById(
                        generatedId);

        assertTrue(result.isPresent());

        assertEquals(
                Long.valueOf(1L),
                result.get().getTreatmentId());

        assertEquals(
                Long.valueOf(3L),
                result.get().getChargeId());

        assertEquals(
                1,
                result.get().getQuantity());

        assertEquals(
                new BigDecimal("5000.00"),
                result.get().getUnitPrice());
    }

    /**
     * Tests returning every charge item
     * belonging to one treatment.
     */
    @Test
    @DisplayName(
            "Should return details belonging to a treatment")
    void shouldReturnDetailsBelongingToTreatment()
            throws SQLException {

        treatmentDetailDAO.save(
                createTreatmentDetail(
                        10L,
                        1L,
                        1,
                        "2000.00",
                        "Consultation"));

        treatmentDetailDAO.save(
                createTreatmentDetail(
                        10L,
                        6L,
                        1,
                        "3000.00",
                        "Dental X-Ray"));

        treatmentDetailDAO.save(
                createTreatmentDetail(
                        20L,
                        2L,
                        1,
                        "3500.00",
                        "Different treatment"));

        List<TreatmentDetail> results =
                treatmentDetailDAO
                        .findByTreatmentId(10L);

        assertEquals(2, results.size());

        assertTrue(
                results.stream().allMatch(
                        detail ->
                                Long.valueOf(10L)
                                        .equals(
                                                detail
                                                        .getTreatmentId())));
    }

    /**
     * Tests updating an existing treatment detail.
     */
    @Test
    @DisplayName(
            "Should update an existing treatment detail")
    void shouldUpdateExistingTreatmentDetail()
            throws SQLException {

        TreatmentDetail treatmentDetail =
                createTreatmentDetail(
                        1L,
                        2L,
                        1,
                        "3500.00",
                        "Original charge");

        long generatedId =
                treatmentDetailDAO.save(
                        treatmentDetail);

        treatmentDetail.setQuantity(3);

        treatmentDetail.setUnitPrice(
                new BigDecimal("3750.00"));

        treatmentDetail.setNotes(
                "Updated charge");

        boolean updated =
                treatmentDetailDAO.update(
                        treatmentDetail);

        assertTrue(updated);

        TreatmentDetail savedDetail =
                treatmentDetailDAO
                        .findById(generatedId)
                        .orElseThrow();

        assertEquals(
                3,
                savedDetail.getQuantity());

        assertEquals(
                new BigDecimal("3750.00"),
                savedDetail.getUnitPrice());

        assertEquals(
                "Updated charge",
                savedDetail.getNotes());
    }

    /**
     * Tests deleting an existing treatment detail.
     */
    @Test
    @DisplayName(
            "Should delete an existing treatment detail")
    void shouldDeleteExistingTreatmentDetail()
            throws SQLException {

        TreatmentDetail treatmentDetail =
                createTreatmentDetail(
                        1L,
                        4L,
                        1,
                        "6000.00",
                        "Extraction charge");

        long generatedId =
                treatmentDetailDAO.save(
                        treatmentDetail);

        boolean deleted =
                treatmentDetailDAO.delete(
                        generatedId);

        assertTrue(deleted);

        assertFalse(
                treatmentDetailDAO
                        .findById(generatedId)
                        .isPresent());
    }

    /**
     * Creates valid treatment detail test data.
     */
    private TreatmentDetail createTreatmentDetail(
            long treatmentId,
            long chargeId,
            int quantity,
            String unitPrice,
            String notes) {

        TreatmentDetail treatmentDetail =
                new TreatmentDetail();

        treatmentDetail.setTreatmentId(
                treatmentId);

        treatmentDetail.setChargeId(
                chargeId);

        treatmentDetail.setQuantity(
                quantity);

        treatmentDetail.setUnitPrice(
                new BigDecimal(unitPrice));

        treatmentDetail.setNotes(notes);

        return treatmentDetail;
    }
}