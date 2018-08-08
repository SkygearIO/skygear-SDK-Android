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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class RequestUnitTest {
    static Map<String, Object> sampleData;
    static ResponseHandler sampleHandler;

    @BeforeClass
    public static void setUpClass() throws Exception {
        RequestUnitTest.sampleData = new HashMap<>();
        RequestUnitTest.sampleData.put("hello", "world");
        RequestUnitTest.sampleData.put("foo", "bar");

        RequestUnitTest.sampleHandler = new ResponseHandler() {
            @Override
            public final void onSuccess(JSONObject result) {
                // Do nothing
            }

            @Override
            public final void onFailure(Error error) {
                // Do nothing
            }
        };
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        RequestUnitTest.sampleData = null;
        RequestUnitTest.sampleHandler = null;
    }

    @Test
    public void testRequestCreateNormalFlow() throws Exception {
        Request req = new Request(
                "test:action",
                RequestUnitTest.sampleData,
                RequestUnitTest.sampleHandler
        );

        assertEquals("test:action", req.action);
        assertEquals(RequestUnitTest.sampleData, req.data);
        assertEquals(RequestUnitTest.sampleHandler, req.getResponseHandler());
    }

    @Test
    public void testRequestCreateNoHandlerFlow() throws Exception {
        Request req = new Request(
                "test:action",
                RequestUnitTest.sampleData
        );

        assertEquals("test:action", req.action);
        assertEquals(RequestUnitTest.sampleData, req.data);
        assertNull(req.getResponseHandler());
    }
}
