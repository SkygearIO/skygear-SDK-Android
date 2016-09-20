package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class AssetPreparePostResponseHandlerUnitTest {
    Asset asset;

    @Before
    public void setUp() throws Exception {
        asset = new Asset(
                "hello.txt",
                "text/plain",
                "hello world".getBytes()
        );
    }

    @After
    public void tearDown() throws Exception {
        asset = null;
    }

    @Test
    public void testAssetPreparePostResponseHandlerSuccessFlow() throws Exception {
        JSONObject result = new JSONObject(
                "{\n" +
                "  \"post-request\": {\n" +
                "    \"action\": \"http://skygear.dev/asset/598bfc9a-ba98-45a5-b194-22ceea2b4954-hello.txt\",\n" +
                "    \"extra-fields\": {\n" +
                "      \"hello\": \"world\",\n" +
                "      \"foo\": \"bar\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"asset\": {\n" +
                "    \"$name\": \"598bfc9a-ba98-45a5-b194-22ceea2b4954-hello.txt\",\n" +
                "    \"$type\": \"asset\",\n" +
                "    \"$url\": \"http://skygear.dev/asset/598bfc9a-ba98-45a5-b194-22ceea2b4954-hello.txt\"\n" +
                "  }\n" +
                "}"
        );

        final boolean[] checkpoints = { false };
        AssetPreparePostResponseHandler handler = new AssetPreparePostResponseHandler(asset) {
            @Override
            public void onPreparePostSuccess(AssetPostRequest postRequest) {
                Asset asset = postRequest.getAsset();
                assertEquals("598bfc9a-ba98-45a5-b194-22ceea2b4954-hello.txt", asset.getName());
                assertEquals("hello world", new String(asset.data));
                assertEquals(11, asset.getSize());
                assertEquals("text/plain", asset.getMimeType());
                assertEquals(
                        "http://skygear.dev/asset/598bfc9a-ba98-45a5-b194-22ceea2b4954-hello.txt",
                        asset.getUrl()
                );

                assertEquals(
                        "http://skygear.dev/asset/598bfc9a-ba98-45a5-b194-22ceea2b4954-hello.txt",
                        postRequest.getAction()
                );

                Map<String, String> extraFields = postRequest.getExtraFields();
                assertEquals(2, extraFields.size());
                assertEquals("world", extraFields.get("hello"));
                assertEquals("bar", extraFields.get("foo"));

                checkpoints[0] = true;
            }

            @Override
            public void onPreparePostFail(String reason) {
                fail("Should not get fail callback");
            }
        };

        handler.onSuccess(result);
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testAssetPreparePostResponseHandlerSuccessWithNoExtraPostFieldsFlow() throws Exception {
        JSONObject result = new JSONObject(
                "{\n" +
                "  \"post-request\": {\n" +
                "    \"action\": \"http://skygear.dev/asset/598bfc9a-ba98-45a5-b194-22ceea2b4954-hello.txt\"\n" +
                "  },\n" +
                "  \"asset\": {\n" +
                "    \"$name\": \"598bfc9a-ba98-45a5-b194-22ceea2b4954-hello.txt\",\n" +
                "    \"$type\": \"asset\",\n" +
                "    \"$url\": \"http://skygear.dev/asset/598bfc9a-ba98-45a5-b194-22ceea2b4954-hello.txt\"\n" +
                "  }\n" +
                "}"
        );

        final boolean[] checkpoints = { false };
        AssetPreparePostResponseHandler handler = new AssetPreparePostResponseHandler(asset) {
            @Override
            public void onPreparePostSuccess(AssetPostRequest postRequest) {
                Asset asset = postRequest.getAsset();
                assertEquals("598bfc9a-ba98-45a5-b194-22ceea2b4954-hello.txt", asset.getName());
                assertEquals("hello world", new String(asset.data));
                assertEquals(11, asset.getSize());
                assertEquals("text/plain", asset.getMimeType());
                assertEquals(
                        "http://skygear.dev/asset/598bfc9a-ba98-45a5-b194-22ceea2b4954-hello.txt",
                        asset.getUrl()
                );

                assertEquals(
                        "http://skygear.dev/asset/598bfc9a-ba98-45a5-b194-22ceea2b4954-hello.txt",
                        postRequest.getAction()
                );

                Map<String, String> extraFields = postRequest.getExtraFields();
                assertEquals(0, extraFields.size());

                checkpoints[0] = true;
            }

            @Override
            public void onPreparePostFail(String reason) {
                fail("Should not get fail callback");
            }
        };

        handler.onSuccess(result);
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testAssetPreparePostResponseHandlerErrorFlow() throws Exception {
        final boolean[] checkpoints = { false };
        AssetPreparePostResponseHandler handler = new AssetPreparePostResponseHandler(asset) {
            @Override
            public void onPreparePostSuccess(AssetPostRequest postRequest) {
                fail("Should not get success callback");
            }

            @Override
            public void onPreparePostFail(String reason) {
                assertEquals("Test Error", reason);
                checkpoints[0] = true;
            }
        };
        handler.onFail(new Request.Error("Test Error"));
        assertTrue(checkpoints[0]);
    }
}
