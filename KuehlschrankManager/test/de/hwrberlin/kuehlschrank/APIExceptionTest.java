package de.hwrberlin.kuehlschrank;

import de.hwrberlin.kuehlschrank.util.APIException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for APIException.
 * Covers: single-argument constructor, two-argument constructor (exception chaining).
 */
class APIExceptionTest {

    @Test
    void constructor_withMessage_storesMessage() {
        APIException ex = new APIException("Network error");
        assertEquals("Network error", ex.getMessage());
    }

    @Test
    void constructor_withMessageAndCause_storesBoth() {
        Throwable cause = new RuntimeException("Connection refused");
        APIException ex = new APIException("API unreachable", cause);
        assertEquals("API unreachable", ex.getMessage());
        assertSame(cause, ex.getCause());
    }

    @Test
    void isRuntimeException() {
        assertInstanceOf(RuntimeException.class, new APIException("test"));
    }
}
