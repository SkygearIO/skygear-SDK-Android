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

package io.skygear.skygear.sso;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class CustomTokenLoginRequestUnitTest {
    @Test
    public void testCustomTokenLoginRequestFlow() throws Exception {
        String tokenString = "eyXXXX";

        CustomTokenLoginRequest req = new CustomTokenLoginRequest("eyXXXX");
        Map<String, Object> data = req.getData();

        assertEquals("sso:custom_token:login", req.action);
        assertEquals("eyXXXX", data.get("token"));
    }

    @Test(expected = InvalidParameterException.class)
    public void testCustomTokenLoginRequestInvalidateTokenNull() throws Exception {
        CustomTokenLoginRequest req = new CustomTokenLoginRequest(null);
        req.validate();
    }
}
