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
public class AuthResponseHandlerUnitTest {

    @Test
    public void testAuthResponseHandlerSuccessFlow() throws Exception {
        final boolean[] checkpoints = { false };
        AuthResponseHandler authResponseHandler = new AuthResponseHandler() {
            @Override
            public void onAuthSuccess(Record user) {
                assertEquals("user", user.getType());
                assertEquals("user-id-1", user.getId());
                assertEquals("user1", user.get("username"));
                assertEquals("user1@skygear.dev", user.get("email"));

                checkpoints[0] = true;
            }

            @Override
            public void onAuthFail(Error error) {
                fail("Should not get error callback");
            }
        };

        authResponseHandler.onSuccess(new JSONObject(
                "{" +
                "  \"access_token\": \"my-token\"," +
                "  \"user_id\": \"user-id-1\"," +
                "  \"username\": \"user1\"," +
                "  \"email\": \"user1@skygear.dev\"," +
                "  \"roles\": [" +
                "    \"Developer\"," +
                "    \"Designer\"" +
                "  ]," +
                "  \"profile\": {" +
                "    \"_type\": \"record\"," +
                "    \"_id\": \"user/user-id-1\"," +
                "    \"_created_by\": \"user-id-1\"," +
                "    \"_ownerID\": \"user-id-1\"," +
                "    \"_updated_by\": \"user-id-1\"," +
                "    \"_access\": null," +
                "    \"_created_at\": \"2006-01-02T15:04:05Z\"," +
                "    \"_updated_at\": \"2006-01-02T15:04:05Z\"," +
                "    \"username\": \"user1\"," +
                "    \"email\": \"user1@skygear.dev\"" +
                "  }" +
                "}"
        ));

        assertTrue(checkpoints[0]);
    }

    @Test
    public void testAuthResponseHandlerFailFlow() throws Exception {
        final boolean[] checkpoints = { false };
        AuthResponseHandler authResponseHandler = new AuthResponseHandler() {
            @Override
            public void onAuthSuccess(Record user) {
                fail("Should not get success callback");
            }

            @Override
            public void onAuthFail(Error error) {
                assertEquals("Test error", error.getDetailMessage());
                checkpoints[0] = true;
            }
        };

        authResponseHandler.onFail(new Error("Test error"));
        assertTrue(checkpoints[0]);
    }
}
