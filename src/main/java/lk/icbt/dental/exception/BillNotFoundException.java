package lk.icbt.dental.exception;

/**
 * Thrown when a requested bill cannot be found.
 */
public class BillNotFoundException
        extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BillNotFoundException(
            String message) {

        super(message);
    }

    public BillNotFoundException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}