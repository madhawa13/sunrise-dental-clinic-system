package lk.icbt.dental.exception;

/**
 * Thrown when a requested treatment record
 * cannot be found.
 */
public class TreatmentNotFoundException
        extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TreatmentNotFoundException(
            String message) {

        super(message);
    }

    public TreatmentNotFoundException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}