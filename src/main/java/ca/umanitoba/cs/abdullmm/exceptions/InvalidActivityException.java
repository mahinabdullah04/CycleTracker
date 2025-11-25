package ca.umanitoba.cs.abdullmm.exceptions;

/**
 * Exception thrown when activity-related operations fail.
 */
public class InvalidActivityException extends Exception {
    public InvalidActivityException(String message) {
        super(message);
    }

    public InvalidActivityException(String message, Throwable cause) {
        super(message, cause);
    }
}
