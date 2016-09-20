package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import com.android.volley.error.VolleyError;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class AssetPostRequestUnitTest {
    Asset asset;

    @Before
    public void setUp() throws Exception {
        asset = new Asset(
                "hello.txt",
                "http://skygear.dev/asset/3f72d553-3aca-4668-8c9f-a454cf7b28e8-hello.txt"
        );
        asset.mimeType = "text/plain";
        asset.data = "hello world".getBytes();
    }

    @After
    public void tearDown() throws Exception {
        asset = null;
    }

    @Test
    public void testAssetPostRequestCreationNormalFlow() throws Exception {
        Map<String, String> extraFields = new HashMap<>();
        extraFields.put("hello", "world");
        extraFields.put("foo", "bar");

        AssetPostRequest postRequest = new AssetPostRequest(
                asset,
                "http://skygear.dev/asset/3f72d553-3aca-4668-8c9f-a454cf7b28e8-hello.txt",
                extraFields
        );

        assertNotNull(postRequest.getAsset());
        assertEquals("hello.txt", postRequest.getAsset().getName());
        assertEquals(
                "http://skygear.dev/asset/3f72d553-3aca-4668-8c9f-a454cf7b28e8-hello.txt",
                postRequest.getAsset().getUrl()
        );

        assertEquals(
                "http://skygear.dev/asset/3f72d553-3aca-4668-8c9f-a454cf7b28e8-hello.txt",
                postRequest.getAction()
        );

        assertNotNull(postRequest.getExtraFields());
        assertEquals(2, postRequest.getExtraFields().size());
        assertEquals("world", postRequest.getExtraFields().get("hello"));
        assertEquals("bar", postRequest.getExtraFields().get("foo"));

        postRequest.validate();
    }

    @Test
    public void testAssetPostRequestCreationWithoutExtraFields() throws Exception {
        AssetPostRequest postRequest = new AssetPostRequest(
                asset,
                "http://skygear.dev/asset/3f72d553-3aca-4668-8c9f-a454cf7b28e8-hello.txt",
                null
        );

        assertNotNull(postRequest.getAsset());
        assertEquals("hello.txt", postRequest.getAsset().getName());
        assertEquals(
                "http://skygear.dev/asset/3f72d553-3aca-4668-8c9f-a454cf7b28e8-hello.txt",
                postRequest.getAsset().getUrl()
        );

        assertEquals(
                "http://skygear.dev/asset/3f72d553-3aca-4668-8c9f-a454cf7b28e8-hello.txt",
                postRequest.getAction()
        );

        assertNotNull(postRequest.getExtraFields());
        assertEquals(0, postRequest.getExtraFields().size());

        postRequest.validate();
    }

    @Test(expected = InvalidParameterException.class)
    public void testAssetPostRequestValidationNotAllowAssetNoData() throws Exception {
        Asset assetWithoutData = new Asset(
                "hello.txt",
                "http://skygear.dev/asset/3f72d553-3aca-4668-8c9f-a454cf7b28e8-hello.txt"
        );
        AssetPostRequest postRequest = new AssetPostRequest(
                assetWithoutData,
                "http://skygear.dev/asset/3f72d553-3aca-4668-8c9f-a454cf7b28e8-hello.txt",
                null
        );

        postRequest.validate();
    }

    @Test
    public void testAssetPostRequestHandleSuccessResponse() throws Exception {
        final Asset assetToUpload = asset;
        AssetPostRequest postRequest = new AssetPostRequest(
                assetToUpload,
                "http://skygear.dev/asset/3f72d553-3aca-4668-8c9f-a454cf7b28e8-hello.txt",
                null
        );

        final boolean[] checkpoints = new boolean[]{ false };
        postRequest.responseHandler = new AssetPostRequest.ResponseHandler() {
            @Override
            public void onPostSuccess(Asset asset, String response) {
                assertEquals(response, "Upload success");

                assertEquals(assetToUpload.getName(), asset.getName());
                assertEquals(assetToUpload.getUrl(), asset.getUrl());
                assertEquals(assetToUpload.getMimeType(), asset.getMimeType());
                assertEquals(assetToUpload.getSize(), asset.getSize());
                assertEquals(assetToUpload.data, asset.data);
                assertFalse(asset.isPendingUpload());

                checkpoints[0] = true;
            }

            @Override
            public void onPostFail(Asset asset, String reason) {
                fail("Should not get fail callback");
            }
        };

        postRequest.onResponse("Upload success");
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testAssetPostRequestHandleErrorResponse() throws Exception {
        final Asset assetToUpload = asset;
        AssetPostRequest postRequest = new AssetPostRequest(
                assetToUpload,
                "http://skygear.dev/asset/3f72d553-3aca-4668-8c9f-a454cf7b28e8-hello.txt",
                null
        );

        final boolean[] checkpoints = new boolean[]{ false };
        postRequest.responseHandler = new AssetPostRequest.ResponseHandler() {
            @Override
            public void onPostSuccess(Asset asset, String response) {
                fail("Should not get success callback");
            }

            @Override
            public void onPostFail(Asset asset, String reason) {
                assertEquals(assetToUpload.getName(), asset.getName());
                assertEquals(assetToUpload.getUrl(), asset.getUrl());
                assertEquals(assetToUpload.getMimeType(), asset.getMimeType());
                assertEquals(assetToUpload.getSize(), asset.getSize());
                assertEquals(assetToUpload.data, asset.data);

                assertEquals("Test Error", reason);

                checkpoints[0] = true;
            }
        };

        postRequest.onErrorResponse(new VolleyError("Test Error"));
        assertTrue(checkpoints[0]);
    }
}