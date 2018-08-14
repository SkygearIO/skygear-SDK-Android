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
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class RegisterDeviceResponseHandlerUnitTest {
    @Test
    public void testRegisterDeviceResponseHandlerSuccessFlow() throws Exception {
        JSONObject result = new JSONObject();
        result.put("id", "test-device-id");

        final boolean[] checkpoints = new boolean[] { false };
        RegisterDeviceResponseHandler handler = new RegisterDeviceResponseHandler() {
            @Override
            public void onRegisterSuccess(String deviceId) {
                assertEquals("test-device-id", deviceId);
                checkpoints[0] = true;
            }

            @Override
            public void onRegisterError(Error error) {
                fail("Should not get fail callback");
            }
        };

        handler.onSuccess(result);
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testRegisterDeviceResponseHandlerFailFlow() throws Exception {
        final boolean[] checkpoints = new boolean[] { false };
        RegisterDeviceResponseHandler handler = new RegisterDeviceResponseHandler() {
            @Override
            public void onRegisterSuccess(String deviceId) {
                fail("Should not get success callback");
            }

            @Override
            public void onRegisterError(Error error) {
                assertEquals("Test error", error.getDetailMessage());
                checkpoints[0] = true;
            }
        };

        handler.onFailure(new Error("Test error"));
        assertTrue(checkpoints[0]);
    }
}
