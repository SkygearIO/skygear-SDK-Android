package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

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

    @Test
    public void testRecordPublicAccessManagement() throws Exception {
        Record aNote = new Record("Note");
        aNote.access = new AccessControl()
                .addEntry(new AccessControl.Entry(AccessControl.Level.READ_WRITE));
        assertTrue(aNote.isPublicReadable());
        assertTrue(aNote.isPublicWritable());

        aNote.setPublicReadOnly();
        assertTrue(aNote.isPublicReadable());
        assertFalse(aNote.isPublicWritable());

        aNote.setPublicNoAccess();
        assertFalse(aNote.isPublicReadable());
        assertFalse(aNote.isPublicWritable());
    }

    @Test
    public void testRecordUserAccessManagement() throws Exception {
        Record aNote = new Record("Note");
        aNote.access = new AccessControl()
                .addEntry(new AccessControl.Entry("user123", AccessControl.Level.READ_WRITE))
                .addEntry(new AccessControl.Entry("user456", AccessControl.Level.READ_ONLY));

        assertTrue(aNote.isReadable("user123"));
        assertTrue(aNote.isWritable("user123"));
        assertTrue(aNote.isReadable("user456"));
        assertFalse(aNote.isWritable("user456"));

        aNote.setReadOnly("user123");
        assertTrue(aNote.isReadable("user123"));
        assertFalse(aNote.isWritable("user123"));
        assertTrue(aNote.isReadable("user456"));
        assertFalse(aNote.isWritable("user456"));

        aNote.setNoAccess("user123");
        assertFalse(aNote.isReadable("user123"));
        assertFalse(aNote.isWritable("user123"));
        assertTrue(aNote.isReadable("user456"));
        assertFalse(aNote.isWritable("user456"));

        aNote.setReadWriteAccess("user456");
        assertFalse(aNote.isReadable("user123"));
        assertFalse(aNote.isWritable("user123"));
        assertTrue(aNote.isReadable("user456"));
        assertTrue(aNote.isWritable("user456"));
    }

    @Test
    public void testRecordRoleAccessManagement() throws Exception {
        Role godRole = new Role("God");
        Role humanRole = new Role("Human");

        Record aNote = new Record("Note");
        aNote.access = new AccessControl()
                .addEntry(new AccessControl.Entry(godRole, AccessControl.Level.READ_WRITE))
                .addEntry(new AccessControl.Entry(humanRole, AccessControl.Level.READ_ONLY));

        assertTrue(aNote.isReadable(godRole));
        assertTrue(aNote.isWritable(godRole));
        assertTrue(aNote.isReadable(humanRole));
        assertFalse(aNote.isWritable(humanRole));

        aNote.setReadOnly(godRole);
        assertTrue(aNote.isReadable(godRole));
        assertFalse(aNote.isWritable(godRole));
        assertTrue(aNote.isReadable(humanRole));
        assertFalse(aNote.isWritable(humanRole));

        aNote.setNoAccess(godRole);
        assertFalse(aNote.isReadable(godRole));
        assertFalse(aNote.isWritable(godRole));
        assertTrue(aNote.isReadable(humanRole));
        assertFalse(aNote.isWritable(humanRole));

        aNote.setReadWriteAccess(humanRole);
        assertFalse(aNote.isReadable(godRole));
        assertFalse(aNote.isWritable(godRole));
        assertTrue(aNote.isReadable(humanRole));
        assertTrue(aNote.isWritable(humanRole));
    }
}
