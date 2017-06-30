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
 * The Auth response handler.
 */
public abstract class AuthResponseHandler implements ResponseHandler {
    /**
     * Auth success callback
     *
     * @param user authenticated user
     */
    public abstract void onAuthSuccess(User user);

    /**
     * Auth fail callback
     *
     * @param error the error
     */
    public abstract void onAuthFail(Error error);

    @Override
    public void onSuccess(JSONObject result) {
        try {
            this.onAuthSuccess(UserSerializer.deserialize(result));
        } catch (JSONException e) {
            this.onAuthFail(new Error("Malformed server response"));
        }
    }

    @Override
    public void onFail(Error error) {
        this.onAuthFail(error);
    }
}
