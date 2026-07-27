package de.hwrberlin.kuehlschrank.util;

/**
 * Custom runtime exception for API/network errors.
 * Lecture 2.1.6: specialised exception classes, exception chaining.
 */
public class APIException extends RuntimeException {
    public APIException(String message) {
        super(message);
    }
    public APIException(String message, Throwable cause) {
        super(message, cause);
    }
}
