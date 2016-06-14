package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import static io.skygear.skygear.Record.Serializer;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class RecordSerializerUnitTest {
    @Test
    public void testRecordTypeValidation() throws Exception {
        assertFalse(Serializer.isValidType(null));
        assertFalse(Serializer.isValidType(""));
        assertFalse(Serializer.isValidType("_note"));

        assertTrue(Serializer.isValidType("note"));
    }

    @Test
    public void testRecordDataKeyValidation() throws Exception {
        assertFalse(Serializer.isValidKey(null));
        assertFalse(Serializer.isValidKey(""));
        assertFalse(Serializer.isValidKey("_id"));
        assertFalse(Serializer.isValidKey("_type"));
        assertFalse(Serializer.isValidKey("_created_at"));
        assertFalse(Serializer.isValidKey("_updated_at"));
        assertFalse(Serializer.isValidKey("_ownerID"));
        assertFalse(Serializer.isValidKey("_created_by"));
        assertFalse(Serializer.isValidKey("_updated_by"));
        assertFalse(Serializer.isValidKey("_access"));

        assertTrue(Serializer.isValidKey("hello"));
    }

    @Test
    public void testRecordDataValueCompatibilityCheck() throws Exception {
        assertFalse(Serializer.isCompatibleValue(null));
        assertFalse(Serializer.isCompatibleValue(new Object()));

        byte b = 12;
        char c = 'a';
        double d = 12.3;
        float f = 12.3f;
        int i = 12;
        long l = 12;
        short s = 12;

        assertTrue(Serializer.isCompatibleValue(b));
        assertTrue(Serializer.isCompatibleValue(c));
        assertTrue(Serializer.isCompatibleValue(d));
        assertTrue(Serializer.isCompatibleValue(f));
        assertTrue(Serializer.isCompatibleValue(i));
        assertTrue(Serializer.isCompatibleValue(l));
        assertTrue(Serializer.isCompatibleValue(s));

        assertTrue(Serializer.isCompatibleValue(false));
        assertTrue(Serializer.isCompatibleValue("okay"));
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

        assertTrue(Serializer.isCompatibleValue(arr1));
        assertFalse(Serializer.isCompatibleValue(arr2));
    }

    @Test
    public void testRecordSerializationNormalFlow() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("hello", "world");
        data.put("foobar", 3);
        data.put("abc", 12.345);

        Record aNote = new Record("Note", data);
        String serializedString = Serializer.serialize(aNote);
        JSONObject jsonObject = new JSONObject(serializedString);

        assertEquals(0, jsonObject.getString("_id").indexOf("Note/"));

        assertEquals("world", jsonObject.getString("hello"));
        assertEquals(3, jsonObject.getInt("foobar"));
        assertEquals(12.345, jsonObject.getDouble("abc"));
    }

    @Test
    public void testRecordDeserializationNormalFlow() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("_id", "Note/48092492-0791-4120-B314-022202AD3971");
        jsonObject.put("hello", "world");
        jsonObject.put("foobar", 3);
        jsonObject.put("abc", 12.345);

        String jsonString = jsonObject.toString();
        Record record = Serializer.deserialize(jsonString);

        assertEquals("Note", record.getType());
        assertEquals("48092492-0791-4120-B314-022202AD3971", record.getId());
        assertEquals("world", record.get("hello"));
        assertEquals(3, record.get("foobar"));
        assertEquals(12.345, record.get("abc"));
    }

    @Test(expected = InvalidParameterException.class)
    public void testRecordDeserializationNotAllowNoId() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("_id", "Note");
        jsonObject.put("hello", "world");
        jsonObject.put("foobar", 3);
        jsonObject.put("abc", 12.345);

        String jsonString = jsonObject.toString();
        Serializer.deserialize(jsonString);
    }
}
