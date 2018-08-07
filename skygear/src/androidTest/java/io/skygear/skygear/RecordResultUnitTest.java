package io.skygear.skygear;

import org.junit.Test;

import static org.junit.Assert.*;

public class RecordResultUnitTest {
    @Test
    public void testValueOnly() {
        RecordResult<String> result = new RecordResult<>("some-value");
        assertEquals("some-value", result.value);
        assertNull(result.error);
        assertFalse(result.isError());
    }

    @Test
    public void testErrorResult() {
        RecordResult<String> result = new RecordResult<>(
                null,
                new Error("some-error")
        );
        assertNull(result.value);
        assertEquals("some-error", result.error.getDetailMessage());
        assertTrue(result.isError());
    }
}
