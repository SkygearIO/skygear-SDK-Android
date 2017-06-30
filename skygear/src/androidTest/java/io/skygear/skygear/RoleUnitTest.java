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

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class RoleUnitTest {
    @Test
    public void testRoleCreation() throws Exception {
        Role godRole = new Role("God");
        assertEquals("God", godRole.getName());
    }

    @Test
    public void testRoleEquality() throws Exception {
        Role godRole1 = new Role("God");
        Role godRole2 = new Role("God");
        assertEquals(godRole1, godRole2);
    }
}
