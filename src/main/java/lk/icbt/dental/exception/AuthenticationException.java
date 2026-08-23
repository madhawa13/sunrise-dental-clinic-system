package lk.icbt.dental.exception;

/**
 * Indicates that a user authentication
 * operation was unsuccessful.
 */
public class AuthenticationException
        extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an authentication exception
     * with an explanatory message.
     *
     * @param message explanation of the failure
     */
    public AuthenticationException(
            String message) {

        super(message);
    }

    /**
     * Creates an authentication exception
     * with a message and original cause.
     *
     * @param message explanation of the failure
     * @param cause original exception
     */
    public AuthenticationException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}