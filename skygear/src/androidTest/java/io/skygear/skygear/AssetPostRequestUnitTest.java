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

import com.android.volley.VolleyError;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
        byte[] data = "hello world".getBytes();
        asset = new Asset.Builder("hello.txt")
                .setMimeType("text/plain")
                .setData(data)
                .build();
        asset.url = "http://skygear.dev/asset/3f72d553-3aca-4668-8c9f-a454cf7b28e8-hello.txt";
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
                "http://skygear.dev/asset/3f72d553-3aca-4668-8c9f-a454cf7b28e8-hello.txt",
                "text/plain"
        );
        AssetPostRequest postRequest = new AssetPostRequest(
                assetWithoutData,
                "http://skygear.dev/asset/3f72d553-3aca-4668-8c9f-a454cf7b28e8-hello.txt",
                null
        );

        postRequest.validate();
    }

    @Test(expected = InvalidParameterException.class)
    public void testAssetPostRequestValidationNotAllowNonTypedAsset() throws Exception {
        byte[] data = "hello world".getBytes();
        Asset assetWithoutType = new Asset.Builder("hello.txt")
                .setMimeType("")
                .setData(data)
                .build();
        assetWithoutType.url = "http://skygear.dev/asset/3f72d553-3aca-4668-8c9f-a454cf7b28e8-hello.txt";

        AssetPostRequest postRequest = new AssetPostRequest(
                assetWithoutType,
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
                assertEquals(assetToUpload.inputStream, asset.inputStream);
                assertFalse(asset.isPendingUpload());

                checkpoints[0] = true;
            }

            @Override
            public void onPostFail(Asset asset, Error error) {
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
            public void onPostFail(Asset asset, Error error) {
                assertEquals(assetToUpload.getName(), asset.getName());
                assertEquals(assetToUpload.getUrl(), asset.getUrl());
                assertEquals(assetToUpload.getMimeType(), asset.getMimeType());
                assertEquals(assetToUpload.getSize(), asset.getSize());
                assertEquals(assetToUpload.inputStream, asset.inputStream);

                assertEquals("Test Error", error.getDetailMessage());

                checkpoints[0] = true;
            }
        };

        postRequest.onErrorResponse(new VolleyError("Test Error"));
        assertTrue(checkpoints[0]);
    }
}