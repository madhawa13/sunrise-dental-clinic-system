package lk.icbt.dental.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import lk.icbt.dental.dao.BillDAO;
import lk.icbt.dental.model.Bill;

/**
 * H2 integration tests for BillDAOImpl.
 */
class BillDAOImplTest {

    private static final String TEST_DATABASE_URL =
            "jdbc:h2:mem:bill_test;"
            + "MODE=MySQL;"
            + "DB_CLOSE_DELAY=-1";

    private BillDAO billDAO;

    /**
     * Creates a fresh bills table before each test.
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
                    "DROP TABLE IF EXISTS bills");

            statement.execute("""
                    CREATE TABLE bills (
                        bill_id BIGINT
                            PRIMARY KEY AUTO_INCREMENT,
                        bill_number VARCHAR(20)
                            NOT NULL UNIQUE,
                        appointment_id BIGINT
                            NOT NULL UNIQUE,
                        subtotal DECIMAL(10,2)
                            NOT NULL,
                        discount DECIMAL(10,2)
                            NOT NULL DEFAULT 0.00,
                        total_amount DECIMAL(10,2)
                            NOT NULL,
                        payment_status VARCHAR(20)
                            NOT NULL DEFAULT 'UNPAID',
                        created_at TIMESTAMP
                            NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP
                            NOT NULL DEFAULT CURRENT_TIMESTAMP
                    )
                    """);
        }

        billDAO =
                new BillDAOImpl(
                        () -> DriverManager.getConnection(
                                TEST_DATABASE_URL));
    }

    @Test
    @DisplayName(
            "Should save bill and return generated ID")
    void shouldSaveBillAndReturnGeneratedId()
            throws SQLException {

        Bill bill = createBill(
                "BILL-TEST-001",
                1001L,
                "5500.00",
                "500.00",
                "5000.00",
                "UNPAID");

        long generatedId =
                billDAO.save(bill);

        assertTrue(generatedId > 0);

        assertEquals(
                Long.valueOf(generatedId),
                bill.getBillId());

        assertNotNull(
                billDAO.findById(generatedId)
                        .orElse(null));
    }

    @Test
    @DisplayName(
            "Should find bill using appointment ID")
    void shouldFindBillUsingAppointmentId()
            throws SQLException {

        Bill bill = createBill(
                "BILL-TEST-002",
                1002L,
                "3500.00",
                "0.00",
                "3500.00",
                "UNPAID");

        billDAO.save(bill);

        Optional<Bill> result =
                billDAO.findByAppointmentId(
                        1002L);

        assertTrue(result.isPresent());

        assertEquals(
                "BILL-TEST-002",
                result.get().getBillNumber());

        assertEquals(
                new BigDecimal("3500.00"),
                result.get().getTotalAmount());
    }

    @Test
    @DisplayName(
            "Should return all bills")
    void shouldReturnAllBills()
            throws SQLException {

        billDAO.save(
                createBill(
                        "BILL-TEST-003",
                        1003L,
                        "2000.00",
                        "0.00",
                        "2000.00",
                        "UNPAID"));

        billDAO.save(
                createBill(
                        "BILL-TEST-004",
                        1004L,
                        "6000.00",
                        "500.00",
                        "5500.00",
                        "UNPAID"));

        List<Bill> bills =
                billDAO.findAll();

        assertEquals(2, bills.size());
    }

    @Test
    @DisplayName(
            "Should update an existing bill")
    void shouldUpdateExistingBill()
            throws SQLException {

        Bill bill = createBill(
                "BILL-TEST-005",
                1005L,
                "5000.00",
                "0.00",
                "5000.00",
                "UNPAID");

        long generatedId =
                billDAO.save(bill);

        bill.setDiscount(
                new BigDecimal("500.00"));

        bill.setTotalAmount(
                new BigDecimal("4500.00"));

        boolean updated =
                billDAO.update(bill);

        assertTrue(updated);

        Bill updatedBill =
                billDAO.findById(generatedId)
                        .orElseThrow();

        assertEquals(
                new BigDecimal("500.00"),
                updatedBill.getDiscount());

        assertEquals(
                new BigDecimal("4500.00"),
                updatedBill.getTotalAmount());
    }

    @Test
    @DisplayName(
            "Should update bill payment status")
    void shouldUpdateBillPaymentStatus()
            throws SQLException {

        Bill bill = createBill(
                "BILL-TEST-006",
                1006L,
                "3000.00",
                "0.00",
                "3000.00",
                "UNPAID");

        long generatedId =
                billDAO.save(bill);

        boolean updated =
                billDAO.updatePaymentStatus(
                        generatedId,
                        "PAID");

        assertTrue(updated);

        Bill updatedBill =
                billDAO.findById(generatedId)
                        .orElseThrow();

        assertEquals(
                "PAID",
                updatedBill.getPaymentStatus());
    }

    /**
     * Creates valid bill test data.
     */
    private Bill createBill(
            String billNumber,
            long appointmentId,
            String subtotal,
            String discount,
            String totalAmount,
            String paymentStatus) {

        Bill bill = new Bill();

        bill.setBillNumber(billNumber);
        bill.setAppointmentId(appointmentId);

        bill.setSubtotal(
                new BigDecimal(subtotal));

        bill.setDiscount(
                new BigDecimal(discount));

        bill.setTotalAmount(
                new BigDecimal(totalAmount));

        bill.setPaymentStatus(
                paymentStatus);

        return bill;
    }
}