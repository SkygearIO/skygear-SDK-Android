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
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SignupRequestUnitTest {
    @Test
    public void testSignupRequestFlow() throws Exception {
        Map authData = new HashMap<>();
        authData.put("username", "user123");
        authData.put("email", "user123@skygear.dev");

        SignupRequest req = new SignupRequest(authData, "123456");
        Map<String, Object> data = req.data;
        Map<String, Object> payloadAuthData = (Map<String, Object>) data.get("auth_data");

        assertEquals("auth:signup", req.action);
        assertEquals("user123", payloadAuthData.get("username"));
        assertEquals("user123@skygear.dev", payloadAuthData.get("email"));
        assertEquals("123456", data.get("password"));
    }

    @Test
    public void testSignupAnonymouslyFlow() throws Exception {
        SignupRequest req = new SignupRequest();
        assertTrue(req.anonymous);
    }

    @Test
    public void testSignupAnonymouslyCanPassValidation() throws Exception {
        SignupRequest req = new SignupRequest();
        req.validate();
    }

    @Test(expected = InvalidParameterException.class)
    public void testSignupRequestInvalidateAuthDataNull() throws Exception {
        SignupRequest req = new SignupRequest(null, "123456");
        req.validate();
    }

    @Test(expected = InvalidParameterException.class)
    public void testSignupRequestInvalidateAuthDataEmpty() throws Exception {
        SignupRequest req = new SignupRequest(new HashMap<String, Object>(), "123456");
        req.validate();
    }

    @Test(expected = InvalidParameterException.class)
    public void testSignupRequestInvalidatePasswordNull() throws Exception {
        Map authData = new HashMap<>();
        authData.put("username", "user123");

        SignupRequest req = new SignupRequest(authData, null);
        req.validate();
    }
}
