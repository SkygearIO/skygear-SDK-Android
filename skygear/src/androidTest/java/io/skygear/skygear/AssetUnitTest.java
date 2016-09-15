package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AssetUnitTest {
    @Test
    public void testAssetCreation() throws Exception {
        byte[] data = "Hello World".getBytes();

        Asset asset = new Asset("hello.txt", "text/plain", data);

        assertEquals("hello.txt", asset.getName());
        assertEquals("text/plain", asset.getMimeType());
        assertEquals(11, asset.getSize());
        assertTrue(asset.isPendingUpload());
        assertNull(asset.getUrl());
    }
}