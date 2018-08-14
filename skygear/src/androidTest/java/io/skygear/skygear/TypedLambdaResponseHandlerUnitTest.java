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

import java.util.Map;
import java.util.List;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class TypedLambdaResponseHandlerUnitTest {
    @Test
    public void testTypedLambdaResponseHandlerMap() throws Exception {
        final boolean[] checkpoints = { false };
        TypedLambdaResponseHandler handler = new TypedLambdaResponseHandler<Map<String, Object>>() {
            @Override
            public void onLambdaSuccess(Map<String, Object> result) {
                assertEquals("world", result.get("hello"));
                checkpoints[0] = true;
            }

            @Override
            public void onLambdaFail(Error error) {
                fail("Should not get fail callback");
            }
        };

        handler.onSuccess(new JSONObject("{\"result\":{\"hello\":\"world\"}}"));
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testTypedLambdaResponseHandlerList() throws Exception {
        final boolean[] checkpoints = { false };
        TypedLambdaResponseHandler handler = new TypedLambdaResponseHandler<List<String>>() {
            @Override
            public void onLambdaSuccess(List<String> result) {
                assertEquals("hello", result.get(0));
                assertEquals("world", result.get(1));
                checkpoints[0] = true;
            }

            @Override
            public void onLambdaFail(Error error) {
                fail("Should not get fail callback");
            }
        };

        handler.onSuccess(new JSONObject("{\"result\":[\"hello\",\"world\"]}"));
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testTypedLambdaResponseHandlerNullResponse() throws Exception {
        final boolean[] checkpoints = { false };
        TypedLambdaResponseHandler handler = new TypedLambdaResponseHandler<Object>() {
            @Override
            public void onLambdaSuccess(Object result) {
                assertNull(result);
                checkpoints[0] = true;
            }

            @Override
            public void onLambdaFail(Error error) {
                fail("Should not get fail callback");
            }
        };

        handler.onSuccess(new JSONObject("{\"result\":null}"));
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testTypedLambdaResponseHandlerFailFlow() throws Exception {
        final boolean[] checkpoints = { false };
        TypedLambdaResponseHandler handler = new TypedLambdaResponseHandler<Object>() {
            @Override
            public void onLambdaSuccess(Object result) {
                fail("Should not get success callback");
            }

            @Override
            public void onLambdaFail(Error error) {
                assertEquals("Test Error", error.getDetailMessage());
                checkpoints[0] = true;
            }
        };

        handler.onFailure(new Error("Test Error"));
        assertTrue(checkpoints[0]);
    }
}
