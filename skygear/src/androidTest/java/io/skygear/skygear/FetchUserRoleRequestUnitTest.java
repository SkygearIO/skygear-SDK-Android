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

import java.util.Map;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class FetchUserRoleRequestUnitTest {
    @Test
    public void testGetUserRoleRequestCreation() throws Exception {
        FetchUserRoleRequest request = new FetchUserRoleRequest(new String[]{"user1", "user2"});

        assertEquals("auth:role:get", request.action);

        Map<String, Object> data = request.data;
        String[] users = (String[]) data.get("users");
        assertEquals(users.length, 2);
        assertEquals(users[0], "user1");
        assertEquals(users[1], "user2");
    }
}
