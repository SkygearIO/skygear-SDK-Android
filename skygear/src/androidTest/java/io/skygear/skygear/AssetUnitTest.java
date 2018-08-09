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

        Asset asset = new Asset.Builder("hello.txt")
                .setMimeType("text/plain")
                .setData(data)
                .build();


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
                "http://skygear.dev/asset/5da73a77-a86a-490b-a8de-d5009f1fb53e-hello.txt",
                "text/plain"
        );

        assertEquals("hello.txt", asset.getName());
        assertEquals("text/plain", asset.getMimeType());
        assertEquals(0, asset.getSize());
        assertFalse(asset.isPendingUpload());
        assertEquals(
                "http://skygear.dev/asset/5da73a77-a86a-490b-a8de-d5009f1fb53e-hello.txt",
                asset.getUrl()
        );
    }
}