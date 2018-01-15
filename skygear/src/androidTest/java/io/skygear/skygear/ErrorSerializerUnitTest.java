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

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ErrorSerializerUnitTest {
    @Test
    public void testErrorSerializationNormalFlow() throws Exception {
        Error error = new Error(102,
                            "PermissionDenied",
                            "write is not allowed",
                            new JSONObject().put("foo", "bar"));
        JSONObject jsonObject = ErrorSerializer.serialize(error);
        assertEquals(jsonObject.getInt("code"), 102);
        assertEquals(jsonObject.getString("name"), "PermissionDenied");
        assertEquals(jsonObject.getString("message"), "write is not allowed");
        assertEquals(jsonObject.getJSONObject("info").getString("foo"), "bar");
    }

    @Test
    public void testErrorDeserializationNormalFlow() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "PermissionDenied");
        jsonObject.put("code", 102);
        jsonObject.put("message", "write is not allowed");
        jsonObject.put("info", new JSONObject().put("foo", "bar"));

        Error error = ErrorSerializer.deserialize(jsonObject);
        assertEquals(error.getCodeValue(), 102);
        assertEquals(error.getName(), "PermissionDenied");
        assertEquals(error.getDetailMessage(), "write is not allowed");
        assertEquals(error.getInfo().getString("foo"), "bar");
    }
}
