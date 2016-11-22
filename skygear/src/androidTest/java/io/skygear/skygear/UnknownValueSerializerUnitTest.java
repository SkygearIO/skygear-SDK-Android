package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class UnknownValueSerializerUnitTest {
    @Test
    public void testUnknownValueSerializationWithUnderlyingType() throws Exception {
        UnknownValue value = new UnknownValue("money");
        JSONObject jsonObject = UnknownValueSerializer.serialize(value);

        assertEquals("unknown", jsonObject.getString("$type"));
        assertEquals("money", jsonObject.getString("$underlying_type"));
    }

    @Test(expected=JSONException.class)
    public void testUnknownValueSerializationWithNullUnderlyingType() throws Exception {
        UnknownValue value = new UnknownValue(null);
        JSONObject jsonObject = UnknownValueSerializer.serialize(value);

        jsonObject.getString("$underlying_type");
    }

    @Test
    public void testUnknownValueDeserializationWithUnderlyingType() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("$type", "unknown");
        jsonObject.put("$underlying_type", "money");

        UnknownValue ref = UnknownValueSerializer.deserialize(jsonObject);
        assertEquals("money", ref.getUnderlyingType());
    }

    @Test
    public void testUnknownValueDeserializationWithNoUnderlyingType() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("$type", "unknown");

        UnknownValue ref = UnknownValueSerializer.deserialize(jsonObject);
        assertNull(null, ref.getUnderlyingType());
    }
}
