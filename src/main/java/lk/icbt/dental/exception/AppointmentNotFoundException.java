package lk.icbt.dental.exception;

/**
 * Thrown when an appointment cannot be found.
 */
public class AppointmentNotFoundException
        extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AppointmentNotFoundException(
            String message) {

        super(message);
    }

    public AppointmentNotFoundException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}