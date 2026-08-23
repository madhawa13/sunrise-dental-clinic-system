package lk.icbt.dental.exception;

/**
 * Thrown when bill information or a billing
 * operation violates a business validation rule.
 */
public class BillValidationException
        extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BillValidationException(
            String message) {

        super(message);
    }

    public BillValidationException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}