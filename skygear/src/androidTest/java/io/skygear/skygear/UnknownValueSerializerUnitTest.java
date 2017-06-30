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
