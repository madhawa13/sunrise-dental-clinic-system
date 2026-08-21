package lk.icbt.dental.exception;

/**
 * Thrown when patient information fails
 * business validation rules.
 */
public class PatientValidationException
        extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PatientValidationException(String message) {
        super(message);
    }

    public PatientValidationException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}