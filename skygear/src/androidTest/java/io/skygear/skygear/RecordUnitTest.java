package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class RecordUnitTest {
    @Test
    public void testRecordInstanceCreationNormalFlow() throws Exception {
        Record aNote = new Record("Note");

        assertNotNull(aNote.getId());
        assertEquals("Note", aNote.getType());
        assertEquals(0, aNote.getData().size());
    }

    @Test
    public void testRecordInstanceCreationWithData() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("hello", "world");
        data.put("foobar", 3);
        data.put("abc", 12.345);

        Record aNote = new Record("Note", data);

        assertNotNull(aNote.getId());
        assertEquals("Note", aNote.getType());
        assertEquals(3, aNote.getData().size());

        assertEquals("world", aNote.get("hello"));
        assertEquals(3, aNote.get("foobar"));
        assertEquals(12.345, aNote.get("abc"));
    }

    @Test(expected = InvalidParameterException.class)
    public void testRecordInstanceCreationNotAllowInvalidType() throws Exception {
        new Record("_note");
    }

    @Test(expected = InvalidParameterException.class)
    public void testRecordInstanceCreationNotAllowReservedKey() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("hello", "world");
        data.put("_type", "note_type");

        new Record("Note", data);
    }

    @Test(expected = InvalidParameterException.class)
    public void testRecordInstanceCreationNotAllowIncompatibleValue() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("hello", "world");
        data.put("foo", new Object());

        new Record("Note", data);
    }

    @Test(expected = InvalidParameterException.class)
    public void testRecordNotAllowSettingReservedKey() throws Exception {
        Record aNote = new Record("Note");
        aNote.set("_type", "note_type");
    }

    @Test(expected = InvalidParameterException.class)
    public void testRecordNotAllowSettingIncompatibleValue() throws Exception {
        Record aNote = new Record("Note");
        aNote.set("foo", new Object());
    }
}
