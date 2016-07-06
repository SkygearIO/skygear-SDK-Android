package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.skygear.skygear.AccessControl.Entry;
import io.skygear.skygear.AccessControl.Level;

import static junit.framework.Assert.assertEquals;
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
        Entry publicReadWrite = new Entry(Level.READ_WRITE);

        assertEquals(Entry.Type.PUBLIC, publicReadWrite.getType());

        // TODO: add test for role-based ACEs
        // TODO: add test for user-based ACEs
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
        // TODO: add test for incompatible comparison
    }

    @Test
    public void testAccessControlCreationFlow() throws Exception {
        AccessControl accessControl = new AccessControl(new Entry[]{
                new Entry(Level.READ_ONLY),
                new Entry(Level.READ_WRITE),
                new Entry(Level.NO_ACCESS)
        });

        // TODO: add test for role-based ACEs
        // TODO: add test for user-based ACEs

        assertEquals(3, accessControl.publicEntryQueue.size());
        assertEquals(new Entry(Level.READ_WRITE), accessControl.publicEntryQueue.peek());
    }

    @Test
    public void testAccessControlAddEntryFlow() throws Exception {
        AccessControl accessControl = new AccessControl(new Entry[]{});
        assertEquals(0, accessControl.publicEntryQueue.size());

        accessControl.addEntry(new Entry(Level.READ_ONLY));
        assertEquals(1, accessControl.publicEntryQueue.size());
        assertEquals(new Entry(Level.READ_ONLY), accessControl.publicEntryQueue.peek());

        // TODO: add test for role-based ACEs
        // TODO: add test for user-based ACEs

        accessControl.addEntry(new Entry(Level.NO_ACCESS));
        assertEquals(2, accessControl.publicEntryQueue.size());
        assertEquals(new Entry(Level.READ_ONLY), accessControl.publicEntryQueue.peek());

        accessControl.addEntry(new Entry(Level.READ_WRITE));
        assertEquals(3, accessControl.publicEntryQueue.size());
        assertEquals(new Entry(Level.READ_WRITE), accessControl.publicEntryQueue.peek());
    }

    @Test
    public void testAccessControlRemoveEntryFlow() throws Exception {
        AccessControl accessControl = new AccessControl(new Entry[]{
                new Entry(Level.READ_ONLY),
                new Entry(Level.READ_WRITE),
                new Entry(Level.NO_ACCESS)
        });
        assertEquals(3, accessControl.publicEntryQueue.size());
        assertEquals(new Entry(Level.READ_WRITE), accessControl.publicEntryQueue.peek());

        // TODO: add test for role-based ACEs
        // TODO: add test for user-based ACEs

        accessControl.removeEntry(new Entry(Level.READ_WRITE));
        assertEquals(2, accessControl.publicEntryQueue.size());
        assertEquals(new Entry(Level.READ_ONLY), accessControl.publicEntryQueue.peek());

        accessControl.removeEntry(new Entry(Level.NO_ACCESS));
        assertEquals(1, accessControl.publicEntryQueue.size());
        assertEquals(new Entry(Level.READ_ONLY), accessControl.publicEntryQueue.peek());

        accessControl.removeEntry(new Entry(Level.READ_ONLY));
        assertEquals(0, accessControl.publicEntryQueue.size());
    }

    @Test
    public void testAccessControlClearEntryFlow() throws Exception {
        AccessControl accessControl = new AccessControl(new Entry[]{
                new Entry(Level.READ_ONLY),
                new Entry(Level.READ_WRITE),
                new Entry(Level.NO_ACCESS)
        });

        // TODO: add test for role-based ACEs
        // TODO: add test for user-based ACEs

        accessControl.clearEntries(Entry.Type.PUBLIC);
        assertEquals(0, accessControl.publicEntryQueue.size());
    }

    @Test
    public void testAccessControlClearAllEntryFlow() throws Exception {
        AccessControl accessControl = new AccessControl(new Entry[]{
                new Entry(Level.READ_ONLY),
                new Entry(Level.READ_WRITE),
                new Entry(Level.NO_ACCESS)
        });

        // TODO: add test for role-based ACEs
        // TODO: add test for user-based ACEs

        accessControl.clearEntries();
        assertEquals(0, accessControl.publicEntryQueue.size());
    }
}
