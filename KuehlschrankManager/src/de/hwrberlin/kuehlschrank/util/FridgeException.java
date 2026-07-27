package de.hwrberlin.kuehlschrank.util;

/**
 * Custom exception class for fridge-related errors.
 * Lecture 2.1.6: specialised exception classes, exception chaining.
 */
public class FridgeException extends RuntimeException {
    public FridgeException(String message)                  { super(message); }
    public FridgeException(String message, Throwable cause) { super(message, cause); }
}
