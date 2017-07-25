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

import java.security.InvalidParameterException;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class AssignUserRoleRequestUnitTest {
    @Test
    public void testAssignUserRoleRequestCreation() throws Exception {
        Record[] users = new Record[]{
                new Record("user", "user1"),
                new Record("user", "user2")
        };

        Role[] roles = new Role[]{
            new Role("Admin"),
            new Role("Developer"),
            new Role("Tester"),
        };

        AssignUserRoleRequest request = new AssignUserRoleRequest(users, roles);

        assertEquals("role:assign", request.action);

        Map<String, Object> data = request.data;
        String[] userIDs = (String[]) data.get("users");
        assertEquals(userIDs.length, 2);
        assertEquals(userIDs[0], "user1");
        assertEquals(userIDs[1], "user2");

        String[] roleNames = (String[]) data.get("roles");
        assertEquals(roleNames.length, 3);
        assertEquals(roleNames[0], "Admin");
        assertEquals(roleNames[1], "Developer");
        assertEquals(roleNames[2], "Tester");
    }

    @Test(expected = InvalidParameterException.class)
    public void testAssignUserRoleRequestInvalidUserRecord() throws Exception {
        new AssignUserRoleRequest(new Record[]{
                new Record("user", "user1"),
                new Record("something", "user2")
        }, new Role[]{});
    }
}
