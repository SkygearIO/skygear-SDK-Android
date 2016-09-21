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
                "http://skygear.dev/asset/928739f5-e4f4-4c1c-9377-a0184dac66eb-hello.txt"
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
    }

    @Test
    public void testAssetDeserializationNormalFlow() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("$type", "asset");
        jsonObject.put("$name", "928739f5-e4f4-4c1c-9377-a0184dac66eb-hello.txt");
        jsonObject.put("$url", "http://skygear.dev/asset/928739f5-e4f4-4c1c-9377-a0184dac66eb-hello.txt");

        Asset asset = AssetSerializer.deserialize(jsonObject);
        assertEquals(
                "928739f5-e4f4-4c1c-9377-a0184dac66eb-hello.txt",
                asset.getName()
        );
        assertEquals(
                "http://skygear.dev/asset/928739f5-e4f4-4c1c-9377-a0184dac66eb-hello.txt",
                asset.getUrl()
        );
    }
}