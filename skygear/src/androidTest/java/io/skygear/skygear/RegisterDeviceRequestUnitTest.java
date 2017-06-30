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

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class RegisterDeviceRequestUnitTest {
    @Test
    public void testRegisterDeviceRequestCreateFlow1() throws Exception {
        RegisterDeviceRequest request = new RegisterDeviceRequest();

        assertEquals("device:register", request.action);

        Map<String, Object> data = request.data;
        assertEquals("android", data.get("type"));
        assertNull(data.get("id"));
        assertNull(data.get("device_token"));
    }

    @Test
    public void testRegisterDeviceRequestCreateFlow2() throws Exception {
        RegisterDeviceRequest request = new RegisterDeviceRequest(
                "testing-device",
                "testing-token",
                "com.example.package"
        );

        assertEquals("device:register", request.action);

        Map<String, Object> data = request.data;
        assertEquals("android", data.get("type"));
        assertEquals("testing-device", data.get("id"));
        assertEquals("testing-token", data.get("device_token"));
        assertEquals("com.example.package", data.get("topic"));
    }
}