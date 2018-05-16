/*
 * Copyright 2017 Oursky Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.skygear.skygear;

import android.location.Location;
import android.support.test.runner.AndroidJUnit4;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ValueSerializerUnitTest {
    @Test
    public void testSerializeNull() throws Exception {
        Object value = null;
        Object jsonValue = ValueSerializer.serialize(value);

        assertEquals(JSONObject.NULL, jsonValue);
    }

    @Test
    public void testDeserializeNull() throws Exception {
        Object value = (Object)ValueSerializer.deserialize(JSONObject.NULL);
        assertNull(value);
    }

    @Test
    public void testSerializeDate() throws Exception {
        Date value = new DateTime(2016, 6, 15, 7, 55, 34, 342, DateTimeZone.UTC).toDate();
        JSONObject jsonValue = (JSONObject)ValueSerializer.serialize(value);

        assertEquals("date", jsonValue.getString("$type"));
        assertEquals("2016-06-15T07:55:34.342Z", jsonValue.getString("$date"));
    }

    @Test
    public void testDeserializeDate() throws Exception {
        JSONObject jsonValue = new JSONObject();
        jsonValue.put("$type", "date");
        jsonValue.put("$date", "2016-06-15T07:55:34.342Z");

        Date value = (Date)ValueSerializer.deserialize(jsonValue);
        assertEquals(
                new DateTime(2016, 6, 15, 7, 55, 34, 342, DateTimeZone.UTC).toDate(),
                value
        );
    }

    @Test
    public void testSerializeAsset() throws Exception {
        Asset value = new Asset(
            "928739f5-e4f4-4c1c-9377-a0184dac66eb-hello.txt",
            "http://skygear.dev/asset/928739f5-e4f4-4c1c-9377-a0184dac66eb-hello.txt",
            "text/plain"
        );
        JSONObject jsonValue = (JSONObject)ValueSerializer.serialize(value);

        assertEquals("asset", jsonValue.getString("$type"));
        assertEquals(
                "928739f5-e4f4-4c1c-9377-a0184dac66eb-hello.txt",
                jsonValue.getString("$name")
        );
        assertEquals(
                "http://skygear.dev/asset/928739f5-e4f4-4c1c-9377-a0184dac66eb-hello.txt",
                jsonValue.getString("$url")
        );
        assertEquals("text/plain", jsonValue.getString("$content_type"));
    }

    @Test
    public void testDeserializeAsset() throws Exception {
        JSONObject jsonValue = new JSONObject();
        jsonValue.put("$type", "asset");
        jsonValue.put("$name", "928739f5-e4f4-4c1c-9377-a0184dac66eb-hello.txt");
        jsonValue.put("$url", "http://skygear.dev/asset/928739f5-e4f4-4c1c-9377-a0184dac66eb-hello.txt");
        jsonValue.put("$content_type", "text/plain");

        Asset value = (Asset)ValueSerializer.deserialize(jsonValue);
        assertEquals("928739f5-e4f4-4c1c-9377-a0184dac66eb-hello.txt", value.getName());
        assertEquals(
                "http://skygear.dev/asset/928739f5-e4f4-4c1c-9377-a0184dac66eb-hello.txt",
                value.getUrl()
        );
    }

    @Test
    public void testSerializeLocation() throws Exception {
        Location value = new Location("skygear");
        value.setLatitude(22.3360901);
        value.setLongitude(114.1476178);
        JSONObject jsonValue = (JSONObject)ValueSerializer.serialize(value);

        assertEquals("geo", jsonValue.getString("$type"));
        assertEquals(22.3360901, jsonValue.getDouble("$lat"));
        assertEquals(114.1476178, jsonValue.getDouble("$lng"));
    }

    @Test
    public void testDeserializeLocation() throws Exception {
        JSONObject jsonValue = new JSONObject("{"
            + "\"$type\": \"geo\","
            + "\"$lat\": 22.3360901,"
            + "\"$lng\": 114.1476178"
            + "}");

        Location value = (Location)ValueSerializer.deserialize((Object)jsonValue);
        assertEquals(22.3360901, value.getLatitude());
        assertEquals(114.1476178, value.getLongitude());
    }

    @Test
    public void testSerializeReference() throws Exception {
        Reference value = new Reference("Comment", "7a7873dc-e14b-4b8f-9c51-948da68e924e");
        JSONObject jsonValue = (JSONObject)ValueSerializer.serialize((Object)value);

        assertEquals("ref", jsonValue.getString("$type"));
        assertEquals("Comment/7a7873dc-e14b-4b8f-9c51-948da68e924e", jsonValue.getString("$id"));
    }

    @Test
    public void testDeserializeReference() throws Exception {
        JSONObject jsonValue = new JSONObject("{"
            + "\"$type\": \"ref\","
            + "\"$id\": \"Comment/7a7873dc-e14b-4b8f-9c51-948da68e924e\""
            + "}");

        Reference value = (Reference)ValueSerializer.deserialize((Object)jsonValue);
        assertEquals("Comment", value.getType());
        assertEquals("7a7873dc-e14b-4b8f-9c51-948da68e924e", value.getId());
    }

    @Test
    public void testSerializeUnknownValue() throws Exception {
        UnknownValue value = new UnknownValue("money");
        JSONObject jsonValue = (JSONObject)ValueSerializer.serialize((Object)value);

        assertEquals("unknown", jsonValue.getString("$type"));
        assertEquals("money", jsonValue.getString("$underlying_type"));
    }

    @Test
    public void testDeserializeUnknownValue() throws Exception {
        JSONObject jsonValue = new JSONObject("{"
            + "\"$type\": \"unknown\","
            + "\"$underlying_type\": \"money\""
            + "}");

        UnknownValue value = (UnknownValue)ValueSerializer.deserialize((Object)jsonValue);
        assertEquals("money", value.getUnderlyingType());
    }

    @Test
    public void testSerializeRecord() throws Exception {
        Record value = new Record("note", "48092492-0791-4120-b314-022202ad3971");
        JSONObject jsonValue = (JSONObject)ValueSerializer.serialize((Object)value);

        assertEquals("record", jsonValue.getString("$type"));
        assertEquals("note/48092492-0791-4120-b314-022202ad3971", jsonValue.getJSONObject("$record").getString("_id"));
    }

    @Test
    public void testDeserializeRecord() throws Exception {
        JSONObject jsonValue = new JSONObject("{"
            + "\"$type\": \"record\","
            + "\"$record\": {"
            + "\"_id\": \"note/48092492-0791-4120-b314-022202ad3971\""
            + "}"
            + "}");

        Record value = (Record)ValueSerializer.deserialize((Object)jsonValue);
        assertEquals("note", value.getType());
        assertEquals("48092492-0791-4120-b314-022202ad3971", value.getId());
    }

    @Test
    public void testSerializeArray() throws Exception {
        Object[] values = new Object[]{
            new Reference("Comment", "7a7873dc-e14b-4b8f-9c51-948da68e924e"),
            new UnknownValue("money")
        };
        JSONArray jsonValue = (JSONArray)ValueSerializer.serialize((Object)values);
        assertEquals(2, jsonValue.length());

        assertEquals("ref", jsonValue.getJSONObject(0).getString("$type"));
        assertEquals("unknown", jsonValue.getJSONObject(1).getString("$type"));
    }

    // NOTE(cheungpat): testDeserializeArray is missing here because an array
    // is always serialized as a list. See testDeserializeList

    @Test
    public void testSerializeList() throws Exception {
        List values = new LinkedList();
        values.add(new Reference("Comment", "7a7873dc-e14b-4b8f-9c51-948da68e924e"));
        values.add(new UnknownValue("money"));
        JSONArray jsonValue = (JSONArray)ValueSerializer.serialize((Object)values);
        assertEquals(2, jsonValue.length());

        assertEquals("ref", jsonValue.getJSONObject(0).getString("$type"));
        assertEquals("unknown", jsonValue.getJSONObject(1).getString("$type"));
    }

    @Test
    public void testDeserializeList() throws Exception {
        JSONArray jsonValue = new JSONArray("["
            + "{\"$type\": \"ref\", \"$id\": \"Comment/7a7873dc-e14b-4b8f-9c51-948da68e924e\"},"
            + "{\"$type\": \"unknown\", \"$underlying_type\": \"money\"}"
            + "]");
        
        List value = (List)ValueSerializer.deserialize((Object)jsonValue);
        assertTrue(value.get(0) instanceof Reference);
        assertTrue(value.get(1) instanceof UnknownValue);
    }

    @Test
    public void testSerializeMap() throws Exception {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("apple", new Reference("Comment", "7a7873dc-e14b-4b8f-9c51-948da68e924e"));
        values.put("orange", new UnknownValue("money"));
        JSONObject jsonValue = (JSONObject)ValueSerializer.serialize((Object)values);

        assertEquals("ref", jsonValue.getJSONObject("apple").getString("$type"));
        assertEquals("unknown", jsonValue.getJSONObject("orange").getString("$type"));
    }

    @Test
    public void testDeserializeMap() throws Exception {
        JSONObject jsonValue = new JSONObject("{"
            + "\"apple\": {\"$type\": \"ref\", \"$id\": \"Comment/7a7873dc-e14b-4b8f-9c51-948da68e924e\"},"
            + "\"orange\": {\"$type\": \"unknown\", \"$underlying_type\": \"money\"}"
            + "}");

        Map value = (Map)ValueSerializer.deserialize((Object)jsonValue);
        assertTrue(value.get("apple") instanceof Reference);
        assertTrue(value.get("orange") instanceof UnknownValue);
    }
}

