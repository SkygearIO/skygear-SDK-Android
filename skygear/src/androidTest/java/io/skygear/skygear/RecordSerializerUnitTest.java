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
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
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
        // prepare reference record data
        Map<String, Object> referenceRecordData = new HashMap<>();
        referenceRecordData.put("okay", "google");
        referenceRecordData.put("hey", "siri");

        // prepare reference record
        Record aComment = new Record("Comment", referenceRecordData);

        // prepare record data
        Map<String, Object> data = new HashMap<>();
        data.put("hello", "world");
        data.put("foobar", 3);
        data.put("abc", 12.345);
        data.put("publish_date", new DateTime(2016, 6, 15, 7, 55, 34, 342, DateTimeZone.UTC).toDate());
        data.put("comment", new Reference(aComment));
        data.put("attachment", new Asset(
                "928739f5-e4f4-4c1c-9377-a0184dac66eb-hello.txt",
                "http://skygear.dev/asset/928739f5-e4f4-4c1c-9377-a0184dac66eb-hello.txt"
        ));

        // prepare record
        Record aNote = new Record("Note", data);
        aNote.ownerId = "user123";
        aNote.creatorId = "user123";
        aNote.updaterId = "user456";
        aNote.createdAt = new DateTime(2016, 6, 14, 4, 55, 34, 342, DateTimeZone.UTC).toDate();
        aNote.updatedAt = new DateTime(2016, 6, 15, 7, 55, 34, 342, DateTimeZone.UTC).toDate();
        aNote.access = new AccessControl()
                .addEntry(new AccessControl.Entry(AccessControl.Level.READ_WRITE));
        aNote.transientMap.put("comment", aComment);

        // assert serialized JSON Object
        JSONObject jsonObject = RecordSerializer.serialize(aNote);

        assertNotNull(jsonObject);
        assertEquals("Note/" + aNote.getId(), jsonObject.getString("_id"));
        assertEquals("user123", jsonObject.getString("_ownerID"));
        assertEquals("user123", jsonObject.getString("_created_by"));
        assertEquals("user456", jsonObject.getString("_updated_by"));
        assertEquals("2016-06-14T04:55:34.342Z", jsonObject.getString("_created_at"));
        assertEquals("2016-06-15T07:55:34.342Z", jsonObject.getString("_updated_at"));

        assertEquals("world", jsonObject.getString("hello"));
        assertEquals(3, jsonObject.getInt("foobar"));
        assertEquals(12.345, jsonObject.getDouble("abc"));

        JSONObject publishDateObject = jsonObject.getJSONObject("publish_date");
        assertEquals("date", publishDateObject.getString("$type"));
        assertEquals("2016-06-15T07:55:34.342Z", publishDateObject.getString("$date"));

        JSONObject commentObject = jsonObject.getJSONObject("comment");
        assertEquals("Comment/" + aComment.getId(), commentObject.getString("$id"));
        assertEquals("ref", commentObject.getString("$type"));

        JSONObject attachmentObject = jsonObject.getJSONObject("attachment");
        assertEquals("asset", attachmentObject.getString("$type"));
        assertEquals(
                "928739f5-e4f4-4c1c-9377-a0184dac66eb-hello.txt",
                attachmentObject.getString("$name")
        );
        assertEquals(
                "http://skygear.dev/asset/928739f5-e4f4-4c1c-9377-a0184dac66eb-hello.txt",
                attachmentObject.getString("$url")
        );

        JSONArray acl = jsonObject.getJSONArray("_access");
        assertEquals(1, acl.length());

        JSONObject publicReadable = acl.getJSONObject(0);
        assertTrue(publicReadable.getBoolean("public"));
        assertEquals("write", publicReadable.getString("level"));

        // assert serialized transient JSON Object
        JSONObject transientObject = jsonObject.getJSONObject("_transient");
        JSONObject commentTransient = transientObject.getJSONObject("comment");

        assertEquals("Comment/" + aComment.getId(), commentTransient.getString("_id"));
        assertEquals("google", commentTransient.getString("okay"));
        assertEquals("siri", commentTransient.getString("hey"));
    }

    @Test
    /* Regression: https://github.com/SkygearIO/skygear-SDK-Android/issues/23 */
    public void testRecordSerializeJsonObject() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("obj", new JSONObject("{ \"hello\": \"world\"}"));

        Record aNote = new Record("Note", data);
        JSONObject jsonObject = RecordSerializer.serialize(aNote);

        assertNotNull(jsonObject);

        JSONObject obj = jsonObject.getJSONObject("obj");
        assertEquals("world", obj.getString("hello"));
    }

    @Test
    /* Regression: https://github.com/SkygearIO/skygear-SDK-Android/issues/23 */
    public void testRecordSerializeEmptyArray() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("empty-array", new String[0]);

        Record aNote = new Record("Note", data);
        JSONObject jsonObject = RecordSerializer.serialize(aNote);

        assertNotNull(jsonObject);

        JSONArray emptyArray = jsonObject.getJSONArray("empty-array");
        assertEquals(0, emptyArray.length());
    }

    @Test
    /* Regression: https://github.com/SkygearIO/skygear-SDK-Android/issues/23 */
    public void testRecordSerializeArray() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("non-empty-array", new String[]{ "hello", "world" });

        Record aNote = new Record("Note", data);
        JSONObject jsonObject = RecordSerializer.serialize(aNote);

        assertNotNull(jsonObject);

        JSONArray nonEmptyArray = jsonObject.getJSONArray("non-empty-array");
        assertEquals(2, nonEmptyArray.length());
        assertEquals("hello", nonEmptyArray.get(0));
        assertEquals("world", nonEmptyArray.get(1));
    }

    @Test
    public void testRecordDeserializationNormalFlow() throws Exception {
        // prepare reference record data
        String referenceRecordId = "7a7873dc-e14b-4b8f-9c51-948da68e924e";
        JSONObject referenceRecordData = new JSONObject();
        referenceRecordData.put("_id", "Comment/" + referenceRecordId);
        referenceRecordData.put("okay", "google");
        referenceRecordData.put("hey", "siri");

        // prepare record data
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("_id", "Note/48092492-0791-4120-B314-022202AD3971");
        jsonObject.put("_created_at", "2016-06-15T07:55:32.342Z");
        jsonObject.put("_created_by", "5a497b0b-cf93-4720-bea4-14637478cfc0");
        jsonObject.put("_ownerID", "5a497b0b-cf93-4720-bea4-14637478cfc1");
        jsonObject.put("_updated_at", "2016-06-15T07:55:33.342Z");
        jsonObject.put("_updated_by", "5a497b0b-cf93-4720-bea4-14637478cfc2");
        jsonObject.put("_access", new JSONArray("[{\"public\":true,\"level\":\"write\"}]"));

        JSONObject transientObject = new JSONObject();
        transientObject.put("comment", referenceRecordData);
        transientObject.put("null-key", JSONObject.NULL);
        jsonObject.put("_transient", transientObject);

        jsonObject.put("hello", "world");
        jsonObject.put("foobar", 3);
        jsonObject.put("abc", 12.345);

        JSONObject publishDateObject = new JSONObject();
        publishDateObject.put("$type", "date");
        publishDateObject.put("$date", "2016-06-15T07:55:34.342Z");
        jsonObject.put("publish_date", publishDateObject);

        JSONObject attachmentObject = new JSONObject();
        attachmentObject.put("$type", "asset");
        attachmentObject.put("$name", "928739f5-e4f4-4c1c-9377-a0184dac66eb-hello.txt");
        attachmentObject.put("$url", "http://skygear.dev/asset/928739f5-e4f4-4c1c-9377-a0184dac66eb-hello.txt");
        jsonObject.put("attachment", attachmentObject);

        JSONObject commentReferenceObject = new JSONObject();
        commentReferenceObject.put("$type", "ref");
        commentReferenceObject.put("$id", "Comment/" + referenceRecordId);
        jsonObject.put("comment", commentReferenceObject);

        // assert deserialized record
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

        assertTrue(record.isPublicWritable());

        assertEquals(
                new DateTime(2016, 6, 15, 7, 55, 34, 342, DateTimeZone.UTC).toDate(),
                record.get("publish_date")
        );

        // assert asset field
        Asset attachment = (Asset) record.get("attachment");
        assertEquals("928739f5-e4f4-4c1c-9377-a0184dac66eb-hello.txt", attachment.getName());
        assertEquals(
                "http://skygear.dev/asset/928739f5-e4f4-4c1c-9377-a0184dac66eb-hello.txt",
                attachment.getUrl()
        );

        // assert reference field
        Reference commentRef = (Reference) record.get("comment");
        assertEquals("Comment", commentRef.getType());
        assertEquals(referenceRecordId, commentRef.getId());

        // assert transient
        Map<String, Record> transientMap = record.getTransient();
        assertEquals(1, transientMap.size());

        Record commentTransient = transientMap.get("comment");
        assertEquals("Comment", commentTransient.getType());
        assertEquals(referenceRecordId, commentTransient.getId());
        assertEquals("google", commentTransient.get("okay"));
        assertEquals("siri", commentTransient.get("hey"));
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

    @Test
    /* Regression: https://github.com/SkygearIO/skygear-SDK-Android/issues/23 */
    public void testRecordDeserializeJsonObject() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("_id", "Note/48092492-0791-4120-B314-022202AD3971");
        jsonObject.put("_created_at", "2016-06-15T07:55:32.342Z");
        jsonObject.put("_created_by", "5a497b0b-cf93-4720-bea4-14637478cfc0");
        jsonObject.put("_ownerID", "5a497b0b-cf93-4720-bea4-14637478cfc1");
        jsonObject.put("_updated_at", "2016-06-15T07:55:33.342Z");
        jsonObject.put("_updated_by", "5a497b0b-cf93-4720-bea4-14637478cfc2");
        jsonObject.put("object", new JSONObject("{\"hello\": \"world\"}"));

        Record record = RecordSerializer.deserialize(jsonObject);
        JSONObject obj = (JSONObject) record.get("object");

        assertEquals("world", obj.getString("hello"));
    }

    @Test
    /* Regression: https://github.com/SkygearIO/skygear-SDK-Android/issues/23 */
    public void testRecordDeserializeArray() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("_id", "Note/48092492-0791-4120-B314-022202AD3971");
        jsonObject.put("_created_at", "2016-06-15T07:55:32.342Z");
        jsonObject.put("_created_by", "5a497b0b-cf93-4720-bea4-14637478cfc0");
        jsonObject.put("_ownerID", "5a497b0b-cf93-4720-bea4-14637478cfc1");
        jsonObject.put("_updated_at", "2016-06-15T07:55:33.342Z");
        jsonObject.put("_updated_by", "5a497b0b-cf93-4720-bea4-14637478cfc2");
        jsonObject.put("array", new JSONArray("[\"hello\", \"world\"]"));

        Record record = RecordSerializer.deserialize(jsonObject);
        JSONArray arr = (JSONArray) record.get("array");

        assertEquals(2, arr.length());
        assertEquals("hello", arr.get(0));
        assertEquals("world", arr.get(1));
    }

    @Test
    /* Regression: https://github.com/SkygearIO/skygear-SDK-Android/issues/44 */
    public void testRecordDeserializeWithNullAccess() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("_id", "Note/48092492-0791-4120-B314-022202AD3971");
        jsonObject.put("_created_at", "2016-06-15T07:55:32.342Z");
        jsonObject.put("_created_by", "5a497b0b-cf93-4720-bea4-14637478cfc0");
        jsonObject.put("_ownerID", "5a497b0b-cf93-4720-bea4-14637478cfc1");
        jsonObject.put("_updated_at", "2016-06-15T07:55:33.342Z");
        jsonObject.put("_updated_by", "5a497b0b-cf93-4720-bea4-14637478cfc2");
        jsonObject.put("_access", JSONObject.NULL);
        jsonObject.put("foo", "bar");

        Record record = RecordSerializer.deserialize(jsonObject);
        assertNull(record.access);
    }

    @Test
    /* Regression: https://github.com/SkygearIO/skygear-SDK-Android/issues/45 */
    public void testRecordDeserializeNotRequiredMetaData() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("_id", "Note/48092492-0791-4120-B314-022202AD3971");
        jsonObject.put("foo", "bar");

        RecordSerializer.deserialize(jsonObject);
    }
}
