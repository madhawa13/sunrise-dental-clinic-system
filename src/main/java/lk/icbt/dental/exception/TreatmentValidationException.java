package lk.icbt.dental.exception;

/**
 * Thrown when treatment information violates
 * a service-layer validation rule.
 */
public class TreatmentValidationException
        extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TreatmentValidationException(
            String message) {

        super(message);
    }

    public TreatmentValidationException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}