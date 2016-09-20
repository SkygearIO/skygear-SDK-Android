package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AssetUnitTest {
    @Test
    public void testAssetCreationFromData() throws Exception {
        byte[] data = "Hello World".getBytes();

        Asset asset = new Asset("hello.txt", "text/plain", data);

        assertEquals("hello.txt", asset.getName());
        assertEquals("text/plain", asset.getMimeType());
        assertEquals(11, asset.getSize());
        assertTrue(asset.isPendingUpload());
        assertNull(asset.getUrl());
    }

    @Test
    public void testAssetCreationFromUrl() throws Exception {
        Asset asset = new Asset(
                "hello.txt",
                "http://skygear.dev/asset/5da73a77-a86a-490b-a8de-d5009f1fb53e-hello.txt"
        );

        assertEquals("hello.txt", asset.getName());
        assertNull(asset.getMimeType());
        assertEquals(0, asset.getSize());
        assertFalse(asset.isPendingUpload());
        assertEquals(
                "http://skygear.dev/asset/5da73a77-a86a-490b-a8de-d5009f1fb53e-hello.txt",
                asset.getUrl()
        );
    }
}