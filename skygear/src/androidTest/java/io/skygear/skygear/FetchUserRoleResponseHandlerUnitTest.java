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

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class FetchUserRoleResponseHandlerUnitTest {

    @Test
    public void testGetUserRoleResponseHandlerSuccessFlow() throws Exception {
        final boolean[] checkpoints = new boolean[] { false };
        FetchUserRoleResponseHandler handler = new FetchUserRoleResponseHandler() {
            @Override
            public void onFetchSuccess(Map<String, Role[]> userRoles) {
                assertEquals(userRoles.size(), 3);
                assertTrue(userRoles.containsKey("user1"));
                assertEquals(userRoles.get("user1").length, 1);
                assertEquals(userRoles.get("user1")[0].getName(), "Developer");
                assertTrue(userRoles.containsKey("user2"));
                assertEquals(userRoles.get("user2").length, 2);
                assertEquals(userRoles.get("user2")[0].getName(), "Admin");
                assertEquals(userRoles.get("user2")[1].getName(), "Tester");
                assertTrue(userRoles.containsKey("user3"));
                assertEquals(userRoles.get("user3").length, 0);

                checkpoints[0] = true;
            }

            @Override
            public void onFetchFail(Error error) {
                fail("Should not get error callback");
            }
        };

        String jsonString = "{" +
                "\"result\": {" +
                "   \"user1\": [\"Developer\"]," +
                "   \"user2\": [\"Admin\", \"Tester\"]," +
                "   \"user3\": []" +
                "}" +
            "}";
        JSONObject result = new JSONObject(jsonString);
        handler.onSuccess(result);

        assertTrue(checkpoints[0]);
    }

    @Test
    public void testGetUserRoleResponseHandlerErrorFlow() throws Exception {
        final boolean[] checkpoints = new boolean[] { false };
        FetchUserRoleResponseHandler handler = new FetchUserRoleResponseHandler() {
            @Override
            public void onFetchSuccess(Map<String, Role[]> userRoles) {
                fail("Should not get success callback");
            }

            @Override
            public void onFetchFail(Error error) {
                assertEquals("Test Error", error.getDetailMessage());
                checkpoints[0] = true;
            }
        };

        handler.onFail(new Error("Test Error"));
        assertTrue(checkpoints[0]);
    }
}
