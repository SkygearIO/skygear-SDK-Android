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
public class AssetSerializerUnitTest {
    @Test
    public void testAssetSerializationNormalFlow() throws Exception {
        JSONObject jsonObject = AssetSerializer.serialize(new Asset(
                "928739f5-e4f4-4c1c-9377-a0184dac66eb-hello.txt",
                "http://skygear.dev/asset/928739f5-e4f4-4c1c-9377-a0184dac66eb-hello.txt",
                "text/plain"
        ));

        assertEquals("asset", jsonObject.getString("$type"));
        assertEquals(
                "928739f5-e4f4-4c1c-9377-a0184dac66eb-hello.txt",
                jsonObject.getString("$name")
        );
        assertEquals(
                "http://skygear.dev/asset/928739f5-e4f4-4c1c-9377-a0184dac66eb-hello.txt",
                jsonObject.getString("$url")
        );
        assertEquals("text/plain", jsonObject.getString("$content_type"));
    }

    @Test
    public void testAssetDeserializationNormalFlow() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("$type", "asset");
        jsonObject.put("$name", "928739f5-e4f4-4c1c-9377-a0184dac66eb-hello.txt");
        jsonObject.put("$url", "http://skygear.dev/asset/928739f5-e4f4-4c1c-9377-a0184dac66eb-hello.txt");
        jsonObject.put("$content_type", "text/plain");

        Asset asset = AssetSerializer.deserialize(jsonObject);
        assertEquals(
                "928739f5-e4f4-4c1c-9377-a0184dac66eb-hello.txt",
                asset.getName()
        );
        assertEquals(
                "http://skygear.dev/asset/928739f5-e4f4-4c1c-9377-a0184dac66eb-hello.txt",
                asset.getUrl()
        );
        assertEquals("text/plain", asset.getMimeType());
    }
}