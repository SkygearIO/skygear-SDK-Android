package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.InvalidParameterException;

import io.skygear.skygear.AccessControl.Entry;
import io.skygear.skygear.AccessControl.Level;
import io.skygear.skygear.AccessControlSerializer.EntrySerializer;
import io.skygear.skygear.AccessControlSerializer.LevelSerializer;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AccessControlSerializerUnitTest {
    @Test
    public void testAccessLevelSerializationNormalFlow() throws Exception {
        assertEquals("write", LevelSerializer.serialize(Level.READ_WRITE));
        assertEquals("read", LevelSerializer.serialize(Level.READ_ONLY));
        assertNull(LevelSerializer.serialize(Level.NO_ACCESS));
    }

    @Test
    public void testAccessLevelDeserialzationNormalFlow() throws Exception {
        assertEquals(Level.READ_WRITE, LevelSerializer.deserialize("write"));
        assertEquals(Level.READ_ONLY, LevelSerializer.deserialize("read"));
        assertEquals(Level.NO_ACCESS, LevelSerializer.deserialize(null));
    }

    @Test(expected = InvalidParameterException.class)
    public void testAccessLevelDeserialzationRejectUnknownLevel() throws Exception {
        LevelSerializer.deserialize("kind-of-write");
    }

    @Test
    public void testAccessControlEntrySerializePublicAccess() throws Exception {
        JSONObject publicReadWriteObject = EntrySerializer.serialize(new Entry(Level.READ_WRITE));
        assertNotNull(publicReadWriteObject);
        assertTrue(publicReadWriteObject.getBoolean("public"));
        assertEquals("write", publicReadWriteObject.getString("level"));


        JSONObject publicReadOnlyObject = EntrySerializer.serialize(new Entry(Level.READ_ONLY));
        assertNotNull(publicReadOnlyObject);
        assertTrue(publicReadOnlyObject.getBoolean("public"));
        assertEquals("read", publicReadOnlyObject.getString("level"));


        JSONObject publicNoAccessObject = EntrySerializer.serialize(new Entry(Level.NO_ACCESS));
        assertNull(publicNoAccessObject);
    }

    @Test
    public void testAccessControlEntryDeserializePublicAccess() throws Exception {
        JSONObject publicReadWriteObject = new JSONObject();
        publicReadWriteObject.put("public", true);
        publicReadWriteObject.put("level", "write");

        Entry publicReadWrite = EntrySerializer.deserialize(publicReadWriteObject);
        assertNotNull(publicReadWrite);
        assertTrue(publicReadWrite.isPublic());
        assertEquals(Level.READ_WRITE, publicReadWrite.getLevel());


        JSONObject publicReadOnlyObject = new JSONObject();
        publicReadOnlyObject.put("public", true);
        publicReadOnlyObject.put("level", "read");

        Entry publicReadOnly = EntrySerializer.deserialize(publicReadOnlyObject);
        assertNotNull(publicReadOnly);
        assertTrue(publicReadOnly.isPublic());
        assertEquals(Level.READ_ONLY, publicReadOnly.getLevel());


        JSONObject publicNoAccessObject = new JSONObject();
        publicNoAccessObject.put("public", true);
        publicNoAccessObject.put("level", JSONObject.NULL);

        Entry publicNoAccess = EntrySerializer.deserialize(publicNoAccessObject);
        assertNotNull(publicNoAccess);
        assertTrue(publicNoAccess.isPublic());
        assertEquals(Level.NO_ACCESS, publicNoAccess.getLevel());
    }

    @Test
    public void testAccessControlSerialization() throws Exception {
        JSONArray jsonArray;
        AccessControl accessControl = new AccessControl()
                .addEntry(new Entry(Level.READ_ONLY))
                .addEntry(new Entry(Level.READ_WRITE))
                .addEntry(new Entry(Level.NO_ACCESS));

        // TODO: Add role-based entries to test serialization
        // TODO: Add user-based entries to test serialization

        jsonArray = AccessControlSerializer.serialize(accessControl);
        assertEquals(1, jsonArray.length());
        assertTrue(jsonArray.getJSONObject(0).getBoolean("public"));
        assertEquals("write", jsonArray.getJSONObject(0).getString("level"));


        accessControl.removeEntry(new Entry(Level.READ_WRITE));
        jsonArray = AccessControlSerializer.serialize(accessControl);
        assertEquals(1, jsonArray.length());
        assertTrue(jsonArray.getJSONObject(0).getBoolean("public"));
        assertEquals("read", jsonArray.getJSONObject(0).getString("level"));


        accessControl.removeEntry(new Entry(Level.READ_ONLY));
        jsonArray = AccessControlSerializer.serialize(accessControl);
        assertEquals(0, jsonArray.length());
    }

    @Test
    public void testAccessControlDeserialization() throws Exception {
        AccessControl accessControl;
        JSONArray jsonArray;

        jsonArray = new JSONArray();
        jsonArray.put(new JSONObject("{\"public\": true, \"level\": \"write\"}"));

        // TODO: Add role-based entries to test deserialization
        // TODO: Add user-based entries to test deserialization

        accessControl = AccessControlSerializer.deserialize(jsonArray);
        assertEquals(1, accessControl.publicEntryQueue.size());
        assertTrue(accessControl.publicEntryQueue.peek().isPublic());
        assertEquals(Level.READ_WRITE, accessControl.publicEntryQueue.peek().getLevel());


        jsonArray = new JSONArray();
        jsonArray.put(new JSONObject("{\"public\": true, \"level\": \"read\"}"));

        accessControl = AccessControlSerializer.deserialize(jsonArray);
        assertEquals(1, accessControl.publicEntryQueue.size());
        assertTrue(accessControl.publicEntryQueue.peek().isPublic());
        assertEquals(Level.READ_ONLY, accessControl.publicEntryQueue.peek().getLevel());


        jsonArray = new JSONArray();

        accessControl = AccessControlSerializer.deserialize(jsonArray);
        assertEquals(0, accessControl.publicEntryQueue.size());
    }
}
