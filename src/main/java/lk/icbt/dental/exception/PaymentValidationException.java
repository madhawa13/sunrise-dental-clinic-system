package lk.icbt.dental.exception;

/**
 * Thrown when payment information violates
 * a payment business rule.
 */
public class PaymentValidationException
        extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PaymentValidationException(
            String message) {

        super(message);
    }

    public PaymentValidationException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}