package de.hwrberlin.kuehlschrank.util;

/**
 * Custom exception class. Lecture 2.1.6: specialised error classes.
 */
public class FridgeException extends RuntimeException {
    public FridgeException(String message)               { super(message); }
    public FridgeException(String message, Throwable cause) { super(message, cause); }
}
