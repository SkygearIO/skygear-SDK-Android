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

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class LambdaResponseHandlerUnitTest {
    @Test
    public void testLambdaResponseHandlerSuccessFlow() throws Exception {
        final boolean[] checkpoints = { false };
        LambdaResponseHandler handler = new LambdaResponseHandler() {
            @Override
            public void onLambdaSuccess(JSONObject result) {
                try {
                    assertEquals("world", result.getString("hello"));
                } catch (JSONException e) {
                    fail(e.getMessage());
                }

                checkpoints[0] = true;
            }

            @Override
            public void onLambdaFail(Error error) {
                fail("Should not get fail callback");
            }
        };

        handler.onSuccess(new JSONObject("{\"hello\":\"world\"}"));
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testLambdaResponseHandlerFailFlow() throws Exception {
        final boolean[] checkpoints = { false };
        LambdaResponseHandler handler = new LambdaResponseHandler() {
            @Override
            public void onLambdaSuccess(JSONObject result) {
                fail("Should not get success callback");
            }

            @Override
            public void onLambdaFail(Error error) {
                assertEquals("Test Error", error.getDetailMessage());
                checkpoints[0] = true;
            }
        };

        handler.onFail(new Error("Test Error"));
        assertTrue(checkpoints[0]);
    }
}