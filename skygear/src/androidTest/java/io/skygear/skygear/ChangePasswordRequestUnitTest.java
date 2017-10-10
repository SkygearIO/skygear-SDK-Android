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
import java.util.Objects;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ChangePasswordRequestUnitTest {
    @Test
    public void testChangePasswordRequestFlow() throws Exception {
        ChangePasswordRequest req = new ChangePasswordRequest("new-password", "old-password");
        Map<String, Object> data = req.data;

        assertEquals("new-password", data.get("password"));
        assertEquals("old-password", data.get("old_password"));
    }

    @Test
    public void testChangePasswordAnonymouslyCanPassValidation() throws Exception {
        ChangePasswordRequest req = new ChangePasswordRequest("new-password", "old-password");
        req.validate();
    }

    @Test(expected = InvalidParameterException.class)
    public void testChangePasswordRequestInvalidateNewPasswordEmpty() throws Exception {
        ChangePasswordRequest req = new ChangePasswordRequest("", "old-password");
        req.validate();
    }

    @Test(expected = InvalidParameterException.class)
    public void testChangePasswordRequestInvalidateNewPasswordNull() throws Exception {
        ChangePasswordRequest req = new ChangePasswordRequest(null, "old-password");
        req.validate();
    }
}
