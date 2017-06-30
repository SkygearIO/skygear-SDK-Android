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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class UserSaveRequestUnitTest {
    @Test
    public void testUserSaveRequestCreationFlow() throws Exception {
        User user = new User("123", "token_123", "user123", "user123@skygear.dev");
        user.addRole(new Role("Citizen"));
        user.addRole(new Role("Programmer"));

        UserSaveRequest request = new UserSaveRequest(user);
        assertEquals("user:update", request.action);

        Map<String, Object> data = request.data;
        assertEquals("123", data.get("_id"));
        assertEquals("user123@skygear.dev", data.get("email"));

        List<String> roleNameList = Arrays.asList((String[]) data.get("roles"));
        assertTrue(roleNameList.contains("Citizen"));
        assertTrue(roleNameList.contains("Programmer"));
    }
}
