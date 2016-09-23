package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ReferenceUnitTest {
    @Test
    public void testReferenceCreationNormalFlow() throws Exception {
        Reference noteRef = new Reference("Note", "123");

        assertEquals("Note", noteRef.type);
        assertEquals("123", noteRef.id);
    }

    @Test
    public void testReferenceCreationUsingRecordFlow() throws Exception {
        Record aNote = new Record("Note", null);
        Reference reference = new Reference(aNote);

        assertEquals(aNote.getId(), reference.id);
        assertEquals(aNote.getType(), reference.type);
    }
}
