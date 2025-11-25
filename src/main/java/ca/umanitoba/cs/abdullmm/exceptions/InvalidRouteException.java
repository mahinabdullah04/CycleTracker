package ca.umanitoba.cs.abdullmm.exceptions;

/**
 * Exception thrown when route-related operations fail.
 */
public class InvalidRouteException extends Exception {
    public InvalidRouteException(String message) {
        super(message);
    }

    public InvalidRouteException(String message, Throwable cause) {
        super(message, cause);
    }
}

