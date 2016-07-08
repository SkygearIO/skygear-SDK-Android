package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.InvalidParameterException;
import java.util.Queue;

import io.skygear.skygear.AccessControl.Entry;
import io.skygear.skygear.AccessControl.Level;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AccessControlUnitTest {
    @Test
    public void testAccessControlPublicEntry() throws Exception {
        Entry publicReadWrite = new Entry(Level.READ_WRITE);
        Entry publicReadOnly = new Entry(Level.READ_ONLY);
        Entry publicNoAccess = new Entry(Level.NO_ACCESS);

        assertTrue(publicReadWrite.isPublic());
        assertEquals(Level.READ_WRITE, publicReadWrite.getLevel());

        assertTrue(publicReadOnly.isPublic());
        assertEquals(Level.READ_ONLY, publicReadOnly.getLevel());

        assertTrue(publicNoAccess.isPublic());
        assertEquals(Level.NO_ACCESS, publicNoAccess.getLevel());
    }

    @Test
    public void testAccessControlEntryGetType() throws Exception {
        assertEquals(Entry.Type.PUBLIC, new Entry(Level.READ_WRITE).getType());
        assertEquals(Entry.Type.USER_BASED, new Entry("user123", Level.READ_WRITE).getType());

        // TODO: add test for role-based ACEs
    }

    @Test
    public void testAccessControlEntryComparison() throws Exception {
        Entry publicReadWrite = new Entry(Level.READ_WRITE);
        Entry publicReadOnly = new Entry(Level.READ_ONLY);
        Entry publicNoAccess = new Entry(Level.NO_ACCESS);

        assertTrue(publicReadWrite.compareTo(publicReadOnly) > 0);
        assertTrue(publicReadOnly.compareTo(publicNoAccess) > 0);
        assertTrue(publicReadWrite.compareTo(publicNoAccess) > 0);

        assertTrue(publicNoAccess.compareTo(publicReadOnly) < 0);
        assertTrue(publicReadOnly.compareTo(publicReadWrite) < 0);
        assertTrue(publicNoAccess.compareTo(publicReadWrite) < 0);

        assertEquals(new Entry(Level.READ_WRITE), publicReadWrite);
        assertEquals(new Entry(Level.READ_ONLY), publicReadOnly);
        assertEquals(new Entry(Level.NO_ACCESS), publicNoAccess);
    }

    @Test
    public void testAccessControlEntryIncompatibleComparison() throws Exception {
        boolean[] checkpoints = { false, false };

        try {
            (new Entry(Level.READ_ONLY)).compareTo(new Entry("user123", Level.READ_ONLY));
        } catch (InvalidParameterException e) {
            checkpoints[0] = true;
        }

        try {
            (new Entry("user123", Level.READ_WRITE)).compareTo(new Entry("user456", Level.READ_ONLY));
        } catch (InvalidParameterException e) {
            checkpoints[1] = true;
        }

        assertTrue(checkpoints[0]);
        assertTrue(checkpoints[1]);
    }

    @Test
    public void testAccessControlCreationFlow() throws Exception {
        AccessControl accessControl = new AccessControl(new Entry[]{
                new Entry(Level.READ_ONLY),
                new Entry(Level.READ_WRITE),
                new Entry(Level.NO_ACCESS),
                new Entry("user123", Level.READ_ONLY),
                new Entry("user123", Level.READ_WRITE),
                new Entry("user456", Level.READ_ONLY)
        });

        // TODO: add test for role-based ACEs

        assertEquals(3, accessControl.publicEntryQueue.size());
        assertEquals(new Entry(Level.READ_WRITE), accessControl.getPublicAccess());

        Queue<Entry> entryQueue1 = accessControl.userEntryMap.get("user123");
        assertEquals(2, entryQueue1.size());
        assertEquals(new Entry("user123", Level.READ_WRITE), accessControl.getAccess("user123"));

        Queue<Entry> entryQueue2 = accessControl.userEntryMap.get("user456");
        assertEquals(1, entryQueue2.size());
        assertEquals(new Entry("user456", Level.READ_ONLY), accessControl.getAccess("user456"));
    }

    @Test
    public void testAccessControlAddEntryFlow() throws Exception {
        AccessControl accessControl = new AccessControl(new Entry[]{});
        assertEquals(0, accessControl.publicEntryQueue.size());
        assertNull(accessControl.userEntryMap.get("user123"));

        accessControl.addEntry(new Entry(Level.READ_ONLY));
        assertEquals(1, accessControl.publicEntryQueue.size());
        assertEquals(new Entry(Level.READ_ONLY), accessControl.getPublicAccess());

        // TODO: add test for role-based ACEs

        accessControl.addEntry(new Entry(Level.NO_ACCESS));
        assertEquals(2, accessControl.publicEntryQueue.size());
        assertEquals(new Entry(Level.READ_ONLY), accessControl.getPublicAccess());

        accessControl.addEntry(new Entry(Level.READ_WRITE));
        assertEquals(3, accessControl.publicEntryQueue.size());
        assertEquals(new Entry(Level.READ_WRITE), accessControl.getPublicAccess());

        accessControl.addEntry(new Entry("user123", Level.READ_ONLY));
        assertEquals(1, accessControl.userEntryMap.get("user123").size());
        assertEquals(new Entry("user123", Level.READ_ONLY), accessControl.getAccess("user123"));

        accessControl.addEntry(new Entry("user123", Level.NO_ACCESS));
        assertEquals(2, accessControl.userEntryMap.get("user123").size());
        assertEquals(new Entry("user123", Level.READ_ONLY), accessControl.getAccess("user123"));

        accessControl.addEntry(new Entry("user123", Level.READ_WRITE));
        assertEquals(3, accessControl.userEntryMap.get("user123").size());
        assertEquals(new Entry("user123", Level.READ_WRITE), accessControl.getAccess("user123"));
    }

    @Test
    public void testAccessControlRemoveEntryFlow() throws Exception {
        AccessControl accessControl = new AccessControl(new Entry[]{
                new Entry(Level.READ_ONLY),
                new Entry(Level.READ_WRITE),
                new Entry(Level.NO_ACCESS),
                new Entry("user123", Level.READ_ONLY),
                new Entry("user123", Level.READ_WRITE),
                new Entry("user123", Level.NO_ACCESS)
        });
        assertEquals(3, accessControl.publicEntryQueue.size());
        assertEquals(new Entry(Level.READ_WRITE), accessControl.getPublicAccess());
        assertEquals(3, accessControl.userEntryMap.get("user123").size());
        assertEquals(new Entry("user123", Level.READ_WRITE), accessControl.getAccess("user123"));

        // TODO: add test for role-based ACEs

        accessControl.removeEntry(new Entry(Level.READ_WRITE));
        assertEquals(2, accessControl.publicEntryQueue.size());
        assertEquals(new Entry(Level.READ_ONLY), accessControl.getPublicAccess());

        accessControl.removeEntry(new Entry(Level.NO_ACCESS));
        assertEquals(1, accessControl.publicEntryQueue.size());
        assertEquals(new Entry(Level.READ_ONLY), accessControl.getPublicAccess());

        accessControl.removeEntry(new Entry(Level.READ_ONLY));
        assertEquals(0, accessControl.publicEntryQueue.size());

        accessControl.removeEntry(new Entry("user123", Level.READ_WRITE));
        assertEquals(2, accessControl.userEntryMap.get("user123").size());
        assertEquals(new Entry("user123", Level.READ_ONLY), accessControl.getAccess("user123"));

        accessControl.removeEntry(new Entry("user123", Level.NO_ACCESS));
        assertEquals(1, accessControl.userEntryMap.get("user123").size());
        assertEquals(new Entry("user123", Level.READ_ONLY), accessControl.getAccess("user123"));

        accessControl.removeEntry(new Entry("user123", Level.READ_ONLY));
        assertEquals(0, accessControl.userEntryMap.get("user123").size());
        assertEquals(new Entry("user123", Level.NO_ACCESS), accessControl.getAccess("user123"));
    }

    @Test
    public void testAccessControlClearEntryFlow() throws Exception {
        AccessControl accessControl = new AccessControl(new Entry[]{
                new Entry(Level.READ_ONLY),
                new Entry(Level.READ_WRITE),
                new Entry(Level.NO_ACCESS),
                new Entry("user123", Level.READ_ONLY),
                new Entry("user123", Level.READ_WRITE),
                new Entry("user456", Level.NO_ACCESS)
        });

        // TODO: add test for role-based ACEs

        accessControl.clearEntries(Entry.Type.PUBLIC);
        assertEquals(0, accessControl.publicEntryQueue.size());

        accessControl.clearEntries(Entry.Type.USER_BASED);
        assertEquals(0, accessControl.userEntryMap.size());
    }

    @Test
    public void testAccessControlClearUserEntryFlow() throws Exception {
        AccessControl accessControl = new AccessControl(new Entry[]{
                new Entry("user123", Level.READ_ONLY),
                new Entry("user123", Level.READ_WRITE),
                new Entry("user456", Level.NO_ACCESS)
        });

        assertEquals(2, accessControl.userEntryMap.get("user123").size());
        assertEquals(1, accessControl.userEntryMap.get("user456").size());

        accessControl.clearEntries("user123");
        assertNull(accessControl.userEntryMap.get("user123"));
        assertEquals(1, accessControl.userEntryMap.get("user456").size());

        accessControl.clearEntries("user456");
        assertNull(accessControl.userEntryMap.get("user123"));
        assertNull(accessControl.userEntryMap.get("user456"));
    }

    @Test
    public void testAccessControlClearAllEntryFlow() throws Exception {
        AccessControl accessControl = new AccessControl(new Entry[]{
                new Entry(Level.READ_ONLY),
                new Entry(Level.READ_WRITE),
                new Entry(Level.NO_ACCESS),
                new Entry("user123", Level.READ_ONLY),
                new Entry("user123", Level.READ_WRITE),
                new Entry("user456", Level.NO_ACCESS)
        });

        // TODO: add test for role-based ACEs

        accessControl.clearEntries();
        assertEquals(0, accessControl.publicEntryQueue.size());
        assertEquals(0, accessControl.userEntryMap.size());
    }
}
