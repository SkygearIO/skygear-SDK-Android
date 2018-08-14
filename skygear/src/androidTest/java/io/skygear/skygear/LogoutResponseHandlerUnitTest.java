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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class LogoutResponseHandlerUnitTest {
    @Test
    public void testLogoutResponseHandlerSuccessFlow() throws Exception {
        final boolean[] checkpoints = { false };
        LogoutResponseHandler logoutResponseHandler = new LogoutResponseHandler() {
            @Override
            public void onLogoutSuccess() {
                checkpoints[0] = true;
            }

            @Override
            public void onLogoutFail(Error error) {
                fail("Should not get error callback");
            }
        };

        logoutResponseHandler.onSuccess(new JSONObject());

        assertTrue(checkpoints[0]);
    }

    @Test
    public void testLogoutResponseHandlerErrorFlow() throws Exception {
        LogoutResponseHandler logoutResponseHandler = new LogoutResponseHandler() {
            @Override
            public void onLogoutSuccess() {
                fail("Should not get success callback");
            }

            @Override
            public void onLogoutFail(Error error) {
                assertEquals("Test Error", error.getDetailMessage());
            }
        };

        logoutResponseHandler.onFailure(new Error("Test Error"));
    }
}
