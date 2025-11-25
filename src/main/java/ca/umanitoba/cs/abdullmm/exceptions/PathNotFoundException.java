package ca.umanitoba.cs.abdullmm.exceptions;

/**
 * Exception thrown when a path cannot be found between two points.
 */
public class PathNotFoundException extends Exception {
    public PathNotFoundException(String message) {
        super(message);
    }

    public PathNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

