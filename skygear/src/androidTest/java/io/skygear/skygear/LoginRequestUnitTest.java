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
public class LoginRequestUnitTest {
    @Test
    public void testLoginRequestUsernameFlow() throws Exception {
        LoginRequest req = new LoginRequest("user123", null, "123456");
        Map<String, Object> data = req.data;

        assertEquals("auth:login", req.action);
        assertEquals("user123", data.get("username"));
        assertEquals("123456", data.get("password"));
    }

    @Test
    public void testLoginRequestEmailFlow() throws Exception {
        LoginRequest req = new LoginRequest(null, "user123@skygear.dev", "123456");
        Map<String, Object> data = req.data;

        assertEquals("auth:login", req.action);
        assertEquals("user123@skygear.dev", data.get("email"));
        assertEquals("123456", data.get("password"));
    }

    @Test(expected = InvalidParameterException.class)
    public void testLoginRequestInvalidateEmailUsernameCoexistence() throws Exception {
        LoginRequest req = new LoginRequest("user123", "user123@skygear.dev", "123456");
        req.validate();
    }

    @Test(expected = InvalidParameterException.class)
    public void testLoginRequestInvalidateEmailUsernameBothNull() throws Exception {
        LoginRequest req = new LoginRequest(null, null, "123456");
        req.validate();
    }

    @Test(expected = InvalidParameterException.class)
    public void testLoginRequestInvalidateUsernameEmpty() throws Exception {
        LoginRequest req = new LoginRequest("", null, "123456");
        req.validate();
    }

    @Test(expected = InvalidParameterException.class)
    public void testLoginRequestInvalidateEmailEmpty() throws Exception {
        LoginRequest req = new LoginRequest(null, "", "123456");
        req.validate();
    }

    @Test(expected = InvalidParameterException.class)
    public void testLoginRequestInvalidatePasswordNull() throws Exception {
        LoginRequest req = new LoginRequest("user123", null, null);
        req.validate();
    }
}
