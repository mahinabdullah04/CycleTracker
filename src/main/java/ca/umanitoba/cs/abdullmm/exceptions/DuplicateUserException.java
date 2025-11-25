package ca.umanitoba.cs.abdullmm.exceptions;

/**
 * Exception thrown when attempting to create a duplicate user.
 */
public class DuplicateUserException extends Exception {
    public DuplicateUserException(String message) {
        super(message);
    }

    public DuplicateUserException(String message, Throwable cause) {
        super(message, cause);
    }
}

