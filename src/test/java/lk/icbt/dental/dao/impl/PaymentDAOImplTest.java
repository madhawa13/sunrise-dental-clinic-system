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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lk.icbt.dental.dao.PaymentDAO;
import lk.icbt.dental.model.Payment;

/**
 * H2 integration tests for PaymentDAOImpl.
 */
class PaymentDAOImplTest {

    private static final String TEST_DATABASE_URL =
            "jdbc:h2:mem:payment_test;"
            + "MODE=MySQL;"
            + "DB_CLOSE_DELAY=-1";

    private PaymentDAO paymentDAO;

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
                    "DROP TABLE IF EXISTS payments");

            statement.execute("""
                    CREATE TABLE payments (
                        payment_id BIGINT
                            PRIMARY KEY AUTO_INCREMENT,
                        payment_number VARCHAR(20)
                            NOT NULL UNIQUE,
                        bill_id BIGINT NOT NULL,
                        amount DECIMAL(10,2) NOT NULL,
                        payment_method VARCHAR(20)
                            NOT NULL,
                        payment_date TIMESTAMP
                            NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        received_by BIGINT NOT NULL,
                        reference_number VARCHAR(100),
                        notes VARCHAR(255)
                    )
                    """);
        }

        paymentDAO =
                new PaymentDAOImpl(
                        () -> DriverManager.getConnection(
                                TEST_DATABASE_URL));
    }

    @Test
    @DisplayName(
            "Should save payment and return generated ID")
    void shouldSavePaymentAndReturnGeneratedId()
            throws SQLException {

        Payment payment =
                createPayment(
                        "PAY-TEST-001",
                        10L,
                        "2500.00",
                        "CASH",
                        1L);

        long generatedId =
                paymentDAO.save(payment);

        assertTrue(generatedId > 0);

        assertEquals(
                Long.valueOf(generatedId),
                payment.getPaymentId());
    }

    @Test
    @DisplayName(
            "Should find payment using ID")
    void shouldFindPaymentUsingId()
            throws SQLException {

        Payment payment =
                createPayment(
                        "PAY-TEST-002",
                        20L,
                        "3000.00",
                        "CARD",
                        1L);

        long generatedId =
                paymentDAO.save(payment);

        Optional<Payment> result =
                paymentDAO.findById(
                        generatedId);

        assertTrue(result.isPresent());

        assertEquals(
                "PAY-TEST-002",
                result.get().getPaymentNumber());

        assertEquals(
                new BigDecimal("3000.00"),
                result.get().getAmount());
    }

    @Test
    @DisplayName(
            "Should return payments belonging to bill")
    void shouldReturnPaymentsBelongingToBill()
            throws SQLException {

        paymentDAO.save(
                createPayment(
                        "PAY-TEST-003",
                        30L,
                        "1000.00",
                        "CASH",
                        1L));

        paymentDAO.save(
                createPayment(
                        "PAY-TEST-004",
                        30L,
                        "1500.00",
                        "CARD",
                        1L));

        paymentDAO.save(
                createPayment(
                        "PAY-TEST-005",
                        40L,
                        "2000.00",
                        "CASH",
                        1L));

        List<Payment> payments =
                paymentDAO.findByBillId(30L);

        assertEquals(2, payments.size());

        assertTrue(
                payments.stream().allMatch(
                        payment ->
                                Long.valueOf(30L)
                                        .equals(
                                                payment
                                                        .getBillId())));
    }

    @Test
    @DisplayName(
            "Should return all payments")
    void shouldReturnAllPayments()
            throws SQLException {

        paymentDAO.save(
                createPayment(
                        "PAY-TEST-006",
                        50L,
                        "1000.00",
                        "CASH",
                        1L));

        paymentDAO.save(
                createPayment(
                        "PAY-TEST-007",
                        60L,
                        "2000.00",
                        "BANK_TRANSFER",
                        1L));

        assertEquals(
                2,
                paymentDAO.findAll().size());
    }

    @Test
    @DisplayName(
            "Should update an existing payment")
    void shouldUpdateExistingPayment()
            throws SQLException {

        Payment payment =
                createPayment(
                        "PAY-TEST-008",
                        70L,
                        "2000.00",
                        "CASH",
                        1L);

        long generatedId =
                paymentDAO.save(payment);

        payment.setAmount(
                new BigDecimal("2500.00"));

        payment.setPaymentMethod(
                "CARD");

        payment.setReferenceNumber(
                "CARD-REF-100");

        boolean updated =
                paymentDAO.update(payment);

        assertTrue(updated);

        Payment updatedPayment =
                paymentDAO
                        .findById(generatedId)
                        .orElseThrow();

        assertEquals(
                new BigDecimal("2500.00"),
                updatedPayment.getAmount());

        assertEquals(
                "CARD",
                updatedPayment.getPaymentMethod());

        assertEquals(
                "CARD-REF-100",
                updatedPayment
                        .getReferenceNumber());
    }

    @Test
    @DisplayName(
            "Should delete an existing payment")
    void shouldDeleteExistingPayment()
            throws SQLException {

        Payment payment =
                createPayment(
                        "PAY-TEST-009",
                        80L,
                        "1000.00",
                        "CASH",
                        1L);

        long generatedId =
                paymentDAO.save(payment);

        boolean deleted =
                paymentDAO.delete(
                        generatedId);

        assertTrue(deleted);

        assertFalse(
                paymentDAO
                        .findById(generatedId)
                        .isPresent());
    }

    private Payment createPayment(
            String paymentNumber,
            long billId,
            String amount,
            String paymentMethod,
            long receivedBy) {

        Payment payment = new Payment();

        payment.setPaymentNumber(
                paymentNumber);

        payment.setBillId(billId);

        payment.setAmount(
                new BigDecimal(amount));

        payment.setPaymentMethod(
                paymentMethod);

        payment.setReceivedBy(
                receivedBy);

        payment.setReferenceNumber(
                "REF-TEST");

        payment.setNotes(
                "Automated payment test");

        return payment;
    }
}