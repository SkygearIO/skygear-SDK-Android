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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class UserUnitTest {
    @Test
    public void testUserModelNormalFlow() throws Exception {
        User user = new User("user_id_001", "my-token", "user_001", "user001@skygear.dev");

        assertEquals("user_id_001", user.id);
        assertEquals("my-token", user.accessToken);
        assertEquals("user_001", user.username);
        assertEquals("user001@skygear.dev", user.email);
    }

    @Test
    public void testUserModelWithoutUsernameEmail() throws Exception {
        User user = new User("user_id_001", "my-token");

        assertEquals("user_id_001", user.id);
        assertEquals("my-token", user.accessToken);
        assertNull(user.username);
        assertNull(user.email);
    }

    @Test
    public void testAddRole() throws Exception {
        User user = new User("user_id_001", "my-token", "user_001", "user001@skygear.dev");
        Role[] userRoles = user.getRoles();
        List<Role> roleList;

        assertEquals(0, userRoles.length);


        user.addRole(new Role("Citizen"));
        user.addRole(new Role("Programmer"));

        userRoles = user.getRoles();
        roleList = Arrays.asList(userRoles);

        assertEquals(2, userRoles.length);
        assertTrue(roleList.contains(new Role("Citizen")));
        assertTrue(roleList.contains(new Role("Programmer")));


        user.addRole(new Role("Citizen"));

        userRoles = user.getRoles();
        roleList = Arrays.asList(userRoles);

        assertEquals(2, userRoles.length);
        assertTrue(roleList.contains(new Role("Citizen")));
        assertTrue(roleList.contains(new Role("Programmer")));
    }

    @Test
    public void testRemoveRole() throws Exception {
        User user = new User("user_id_001", "my-token", "user_001", "user001@skygear.dev");
        user.addRole(new Role("Citizen"));
        user.addRole(new Role("Programmer"));
        assertEquals(2, user.getRoles().length);

        user.removeRole(new Role("Programmer"));
        assertEquals(1, user.getRoles().length);

        user.removeRole(new Role("Citizen"));
        assertEquals(0, user.getRoles().length);
    }

    @Test
    public void testHasRole() throws Exception {
        User user = new User("user_id_001", "my-token", "user_001", "user001@skygear.dev");
        assertFalse(user.hasRole(new Role("Citizen")));
        assertFalse(user.hasRole(new Role("Programmer")));

        user.addRole(new Role("Citizen"));
        assertTrue(user.hasRole(new Role("Citizen")));
        assertFalse(user.hasRole(new Role("Programmer")));

        user.addRole(new Role("Programmer"));
        assertTrue(user.hasRole(new Role("Citizen")));
        assertTrue(user.hasRole(new Role("Programmer")));
    }
}
