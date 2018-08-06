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
 * The Skygear Unregister Device Response Handler.
 */
public abstract class UnregisterDeviceResponseHandler extends ResponseHandler {

    /**
     * Unregister success callback.
     *
     * @param deviceId the device id
     */
    public abstract void onUnregisterSuccess(String deviceId);

    /**
     * Unregister error callback.
     *
     * @param error the error
     */
    public abstract void onUnregisterError(Error error);

    @Override
    public final void onSuccess(JSONObject result) {
        try {
            String deviceId = result.getString("id");
            this.onUnregisterSuccess(deviceId);
        } catch (JSONException e) {
            this.onUnregisterError(new Error("Malformed server response"));
        }
    }

    @Override
    public final void onFail(Error error) {
        this.onUnregisterError(error);
    }
}
