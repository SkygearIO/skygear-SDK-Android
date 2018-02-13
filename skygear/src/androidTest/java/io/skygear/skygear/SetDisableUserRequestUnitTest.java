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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertFalse;

@RunWith(AndroidJUnit4.class)
public class SetDisableUserRequestUnitTest {
    @Test
    public void testCreateEnable() throws Exception {
        SetDisableUserRequest request = SetDisableUserRequest.enableUserRequest("user1");

        assertEquals("auth:disable:set", request.action);

        Map<String, Object> data = request.data;
        assertEquals("user1", data.get("auth_id"));
        assertFalse((boolean)data.get("disabled"));
    }

    @Test
    public void testCreateDisable() throws Exception {
        Date expiry = new DateTime(2016, 6, 15, 7, 55, 34, 342, DateTimeZone.UTC).toDate();
        SetDisableUserRequest request = SetDisableUserRequest.disableUserRequest("user1", "some reason", expiry);

        assertEquals("auth:disable:set", request.action);

        Map<String, Object> data = request.data;
        assertEquals("user1", data.get("auth_id"));
        assertTrue((boolean)data.get("disabled"));
        assertEquals("some reason", data.get("message"));
        assertEquals("2016-06-15T07:55:34.342Z", data.get("expiry"));
    }

    @Test
    public void testCreateDisableWithOptionalParameters() throws Exception {
        Date expiry = new DateTime(2016, 6, 15, 7, 55, 34, 342, DateTimeZone.UTC).toDate();
        SetDisableUserRequest request = SetDisableUserRequest.disableUserRequest("user1");

        assertEquals("auth:disable:set", request.action);

        Map<String, Object> data = request.data;
        assertEquals("user1", data.get("auth_id"));
        assertTrue((boolean)data.get("disabled"));
    }
}
