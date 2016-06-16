package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class RecordSerializerUnitTest {
    @Test
    public void testRecordTypeValidation() throws Exception {
        assertFalse(RecordSerializer.isValidType(null));
        assertFalse(RecordSerializer.isValidType(""));
        assertFalse(RecordSerializer.isValidType("_note"));

        assertTrue(RecordSerializer.isValidType("note"));
    }

    @Test
    public void testRecordDataKeyValidation() throws Exception {
        assertFalse(RecordSerializer.isValidKey(null));
        assertFalse(RecordSerializer.isValidKey(""));
        assertFalse(RecordSerializer.isValidKey("_id"));
        assertFalse(RecordSerializer.isValidKey("_type"));
        assertFalse(RecordSerializer.isValidKey("_created_at"));
        assertFalse(RecordSerializer.isValidKey("_updated_at"));
        assertFalse(RecordSerializer.isValidKey("_ownerID"));
        assertFalse(RecordSerializer.isValidKey("_created_by"));
        assertFalse(RecordSerializer.isValidKey("_updated_by"));
        assertFalse(RecordSerializer.isValidKey("_access"));

        assertTrue(RecordSerializer.isValidKey("hello"));
    }

    @Test
    public void testRecordDataValueCompatibilityCheck() throws Exception {
        assertFalse(RecordSerializer.isCompatibleValue(null));
        assertFalse(RecordSerializer.isCompatibleValue(new Object()));

        byte b = 12;
        char c = 'a';
        double d = 12.3;
        float f = 12.3f;
        int i = 12;
        long l = 12;
        short s = 12;

        assertTrue(RecordSerializer.isCompatibleValue(b));
        assertTrue(RecordSerializer.isCompatibleValue(c));
        assertTrue(RecordSerializer.isCompatibleValue(d));
        assertTrue(RecordSerializer.isCompatibleValue(f));
        assertTrue(RecordSerializer.isCompatibleValue(i));
        assertTrue(RecordSerializer.isCompatibleValue(l));
        assertTrue(RecordSerializer.isCompatibleValue(s));

        assertTrue(RecordSerializer.isCompatibleValue(false));
        assertTrue(RecordSerializer.isCompatibleValue("okay"));
    }

    @Test
    public void testRecordDataValueCompatibilityCheckWithArray() throws Exception {
        byte b = 12;
        char c = 'a';
        double d = 12.3;
        float f = 12.3f;
        int i = 12;
        long l = 12;
        short s = 12;

        Object[] arr1 = new Object[] { b, c, d, f, i, l, s };
        Object[] arr2 = new Object[] { "hello", "world", new Object() };

        assertTrue(RecordSerializer.isCompatibleValue(arr1));
        assertFalse(RecordSerializer.isCompatibleValue(arr2));
    }

    @Test
    public void testRecordSerializationNormalFlow() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("hello", "world");
        data.put("foobar", 3);
        data.put("abc", 12.345);
        data.put("publish_date", new DateTime(2016, 6, 15, 7, 55, 34, 342, DateTimeZone.UTC).toDate());

        Record aNote = new Record("Note", data);
        JSONObject jsonObject = RecordSerializer.serialize(aNote);

        assertEquals(0, jsonObject.getString("_id").indexOf("Note/"));

        assertEquals("world", jsonObject.getString("hello"));
        assertEquals(3, jsonObject.getInt("foobar"));
        assertEquals(12.345, jsonObject.getDouble("abc"));

        JSONObject publishDateObject = jsonObject.getJSONObject("publish_date");
        assertEquals("date", publishDateObject.getString("$type"));
        assertEquals("2016-06-15T07:55:34.342Z", publishDateObject.getString("$date"));

        JSONArray acl = jsonObject.getJSONArray("_access");
        assertEquals(1, acl.length());

        JSONObject publicReadable = acl.getJSONObject(0);
        assertTrue(publicReadable.getBoolean("public"));
        assertEquals("read", publicReadable.getString("level"));
    }

    @Test
    public void testRecordDeserializationNormalFlow() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("_id", "Note/48092492-0791-4120-B314-022202AD3971");
        jsonObject.put("_created_at", "2016-06-15T07:55:32.342Z");
        jsonObject.put("_created_by", "5a497b0b-cf93-4720-bea4-14637478cfc0");
        jsonObject.put("_ownerID", "5a497b0b-cf93-4720-bea4-14637478cfc1");
        jsonObject.put("_updated_at", "2016-06-15T07:55:33.342Z");
        jsonObject.put("_updated_by", "5a497b0b-cf93-4720-bea4-14637478cfc2");
        jsonObject.put("_access", null);
        jsonObject.put("hello", "world");
        jsonObject.put("foobar", 3);
        jsonObject.put("abc", 12.345);

        JSONObject publishDateObject = new JSONObject();
        publishDateObject.put("$type", "date");
        publishDateObject.put("$date", "2016-06-15T07:55:34.342Z");

        jsonObject.put("publish_date", publishDateObject);

        Record record = RecordSerializer.deserialize(jsonObject);

        assertEquals("Note", record.getType());
        assertEquals("48092492-0791-4120-B314-022202AD3971", record.getId());
        assertEquals("5a497b0b-cf93-4720-bea4-14637478cfc0", record.getCreatorId());
        assertEquals("5a497b0b-cf93-4720-bea4-14637478cfc1", record.getOwnerId());
        assertEquals("5a497b0b-cf93-4720-bea4-14637478cfc2", record.getUpdaterId());

        assertEquals(
                new DateTime(2016, 6, 15, 7, 55, 32, 342, DateTimeZone.UTC).toDate(),
                record.getCreatedAt()
        );
        assertEquals(
                new DateTime(2016, 6, 15, 7, 55, 33, 342, DateTimeZone.UTC).toDate(),
                record.getUpdatedAt()
        );

        assertEquals("world", record.get("hello"));
        assertEquals(3, record.get("foobar"));
        assertEquals(12.345, record.get("abc"));

        assertEquals(
                new DateTime(2016, 6, 15, 7, 55, 34, 342, DateTimeZone.UTC).toDate(),
                record.get("publish_date")
        );
    }

    @Test(expected = InvalidParameterException.class)
    public void testRecordDeserializationNotAllowNoId() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("_id", "Note");
        jsonObject.put("hello", "world");
        jsonObject.put("foobar", 3);
        jsonObject.put("abc", 12.345);

        RecordSerializer.deserialize(jsonObject);
    }
}
