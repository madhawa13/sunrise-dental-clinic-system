package lk.icbt.dental.exception;

/**
 * Thrown when appointment information
 * fails business validation rules.
 */
public class AppointmentValidationException
        extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AppointmentValidationException(
            String message) {

        super(message);
    }

    public AppointmentValidationException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}