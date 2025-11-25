package ca.umanitoba.cs.abdullmm.exceptions;

/**
 * Exception thrown when user-related operations fail.
 */
public class InvalidUserException extends Exception {
    public InvalidUserException(String message) {
        super(message);
    }

    public InvalidUserException(String message, Throwable cause) {
        super(message, cause);
    }
}

