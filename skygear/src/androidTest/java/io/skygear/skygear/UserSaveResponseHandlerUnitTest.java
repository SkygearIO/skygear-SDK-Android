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

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class UserSaveResponseHandlerUnitTest {
    @Test
    public void testUserSaveResponseHandlerSuccessFlow() throws Exception {
        final boolean[] checkpoints = { false };
        UserSaveResponseHandler handler = new UserSaveResponseHandler() {
            @Override
            public void onSaveSuccess(User user) {
                assertEquals("001", user.getId());
                assertEquals("user01@skygear.dev", user.getEmail());
                assertEquals("user01", user.getUsername());

                assertTrue(user.hasRole(new Role("Citizen")));
                assertTrue(user.hasRole(new Role("Programmer")));

                checkpoints[0] = true;
            }

            @Override
            public void onSaveFail(Error error) {
                fail("Should not get fail callback");
            }
        };

        JSONObject userObject = new JSONObject();
        userObject.put("_id", "001");
        userObject.put("email", "user01@skygear.dev");
        userObject.put("username", "user01");
        userObject.put("roles", new JSONArray("[\"Citizen\", \"Programmer\"]"));

        handler.onSuccess(userObject);
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testUserSaveResponseHandlerFailFlow() throws Exception {
        final boolean[] checkpoints = { false };
        UserSaveResponseHandler handler = new UserSaveResponseHandler() {
            @Override
            public void onSaveSuccess(User user) {
                fail("Should not get success callback");
            }

            @Override
            public void onSaveFail(Error error) {
                assertEquals("Test Error", error.getDetailMessage());
                checkpoints[0] = true;
            }
        };

        handler.onFail(new Error("Test Error"));
        assertTrue(checkpoints[0]);
    }
}
