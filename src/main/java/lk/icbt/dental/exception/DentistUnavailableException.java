package lk.icbt.dental.exception;

/**
 * Thrown when a dentist already has an appointment
 * for the selected date and time.
 */
public class DentistUnavailableException
        extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DentistUnavailableException(
            String message) {

        super(message);
    }

    public DentistUnavailableException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}