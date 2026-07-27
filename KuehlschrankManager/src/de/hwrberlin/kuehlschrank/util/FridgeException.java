package de.hwrberlin.kuehlschrank.util;

/**
 * Application-specific runtime exception.
 * Lecture 2.1.9: Custom exception classes (unchecked, extends RuntimeException).
 */
public class FridgeException extends RuntimeException {
    public FridgeException(String message)            { super(message); }
    public FridgeException(String message, Throwable cause) { super(message, cause); }
}
