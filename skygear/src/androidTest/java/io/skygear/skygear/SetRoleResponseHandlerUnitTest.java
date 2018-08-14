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

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class SetRoleResponseHandlerUnitTest {

    @Test
    public void testSetRoleResponseHandlerSuccessFlow() throws Exception {
        final boolean[] checkpoints = new boolean[] { false };
        SetRoleResponseHandler handler = new SetRoleResponseHandler() {
            @Override
            public void onSetSuccess(Role[] roles) {
                assertEquals(2, roles.length);
                List<Role> roleList = Arrays.asList(roles);

                assertTrue(roleList.contains(new Role("God")));
                assertTrue(roleList.contains(new Role("Boss")));

                checkpoints[0] = true;
            }

            @Override
            public void onSetFail(Error error) {
                fail("Should not get error callback");
            }
        };

        JSONObject result = new JSONObject("{\"result\":[\"God\",\"Boss\"]}");
        handler.onSuccess(result);

        assertTrue(checkpoints[0]);
    }

    @Test
    public void testSetRoleResponseHandlerErrorFlow() throws Exception {
        final boolean[] checkpoints = new boolean[] { false };
        SetRoleResponseHandler handler = new SetRoleResponseHandler() {
            @Override
            public void onSetSuccess(Role[] roles) {
                fail("Should not get success callback");
            }

            @Override
            public void onSetFail(Error error) {
                assertEquals("Test Error", error.getDetailMessage());
                checkpoints[0] = true;
            }
        };

        handler.onFailure(new Error("Test Error"));
        assertTrue(checkpoints[0]);
    }
}