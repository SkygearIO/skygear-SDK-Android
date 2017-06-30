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

import java.util.HashMap;

/**
 * The Skygear Register Device Request.
 */
public class RegisterDeviceRequest extends Request {
    /**
     * Instantiates a new Skygear Register Device Request.
     */
    public RegisterDeviceRequest() {
        super("device:register");

        this.data = new HashMap<>();
        this.data.put("type", "android");
    }

    /**
     * Instantiates a new Skygear Register Device Request with
     * Device ID and Device Token
     *
     * @deprecated use {@link #RegisterDeviceRequest(String, String, String)} instead.
     *
     * @param deviceId    the device id
     * @param deviceToken the device token
     */
    @Deprecated
    public RegisterDeviceRequest(String deviceId, String deviceToken) {
        this();

        if (deviceId != null) {
            this.data.put("id", deviceId);
        }

        if (deviceToken != null) {
            this.data.put("device_token", deviceToken);
        }
    }

    /**
     * Instantiates a new Skygear Register Device Request with
     * Device ID, Device Token and Package Name
     *
     * @param deviceId    the device id
     * @param deviceToken the device token
     * @param topic       the topic, should equal to the package name of the application
     */
    public RegisterDeviceRequest(String deviceId, String deviceToken, String topic) {
        this();

        if (deviceId != null) {
            this.data.put("id", deviceId);
        }

        if (deviceToken != null) {
            this.data.put("device_token", deviceToken);
        }

        if (topic != null) {
            this.data.put("topic", topic);
        }
    }
}
