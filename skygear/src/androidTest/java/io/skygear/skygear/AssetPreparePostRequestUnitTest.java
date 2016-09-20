package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.InvalidParameterException;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class AssetPreparePostRequestUnitTest {
    @Test
    public void testAssetPreparePostRequestNormalFlow() throws Exception {
        Asset asset = new Asset("hello.txt", "text/plain", "Hello World".getBytes());
        AssetPreparePostRequest request = new AssetPreparePostRequest(asset);

        assertEquals("asset:put", request.action);
        assertEquals("hello.txt", request.data.get("filename"));
        assertEquals("text/plain", request.data.get("content-type"));
        assertEquals(11L, request.data.get("content-size"));
    }

    @Test(expected = InvalidParameterException.class)
    public void testAssetPreparePostRequestNotAllowNoFilename() throws Exception {
        Asset asset = new Asset(null, "text/plain", "Hello World".getBytes());
        AssetPreparePostRequest request = new AssetPreparePostRequest(asset);

        request.validate();
    }

    @Test(expected = InvalidParameterException.class)
    public void testAssetPreparePostRequestNotAllowNoMimeType() throws Exception {
        Asset asset = new Asset("hello.txt", null, "Hello World".getBytes());
        AssetPreparePostRequest request = new AssetPreparePostRequest(asset);

        request.validate();
    }

    @Test(expected = InvalidParameterException.class)
    public void testAssetPreparePostRequestNotAllowNoData() throws Exception {
        Asset asset = new Asset("hello.txt", "text/plain", null);
        AssetPreparePostRequest request = new AssetPreparePostRequest(asset);

        request.validate();
    }
}
