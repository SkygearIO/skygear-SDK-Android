package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ReferenceSerializerUnitTest {
    @Test
    public void testReferenceSerializationNormalFlow() throws Exception {
        Reference ref = new Reference("Note", "c71c6ce4-a3c7-4b7d-833b-46b7df8cec03");
        JSONObject jsonObject = ReferenceSerializer.serialize(ref);

        assertEquals("ref", jsonObject.getString("$type"));
        assertEquals("Note/c71c6ce4-a3c7-4b7d-833b-46b7df8cec03", jsonObject.getString("$id"));
    }

    @Test
    public void testReferenceDeserializationNormalFlow() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("$type", "ref");
        jsonObject.put("$id", "Note/c71c6ce4-a3c7-4b7d-833b-46b7df8cec03");

        Reference ref = ReferenceSerializer.deserialize(jsonObject);
        assertEquals("Note", ref.getType());
        assertEquals("c71c6ce4-a3c7-4b7d-833b-46b7df8cec03", ref.getId());
    }
}
