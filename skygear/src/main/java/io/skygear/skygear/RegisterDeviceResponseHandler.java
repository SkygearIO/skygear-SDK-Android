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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Skygear Register Device Response Handler.
 */
public abstract class RegisterDeviceResponseHandler implements ResponseHandler {

    /**
     * Register success callback.
     *
     * @param deviceId the device id
     */
    public abstract void onRegisterSuccess(String deviceId);

    /**
     * Register error callback.
     *
     * @param error the error
     */
    public abstract void onRegisterError(Error error);

    @Override
    public void onSuccess(JSONObject result) {
        try {
            String deviceId = result.getString("id");
            this.onRegisterSuccess(deviceId);
        } catch (JSONException e) {
            this.onRegisterError(new Error("Malformed server response"));
        }
    }

    @Override
    public void onFail(Error error) {
        this.onRegisterError(error);
    }
}
