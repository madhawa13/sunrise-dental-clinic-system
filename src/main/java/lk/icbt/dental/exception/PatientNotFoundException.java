package lk.icbt.dental.exception;

/**
 * Thrown when a requested patient cannot be found.
 */
public class PatientNotFoundException
        extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PatientNotFoundException(String message) {
        super(message);
    }

    public PatientNotFoundException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}