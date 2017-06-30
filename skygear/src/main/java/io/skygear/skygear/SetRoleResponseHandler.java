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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Skygear Role Setup Response Handler.
 */
public abstract class SetRoleResponseHandler implements ResponseHandler {
    /**
     * Setup success callback.
     *
     * @param roles the roles being set
     */
    public abstract void onSetSuccess(Role[] roles);

    /**
     * Setup fail callback.
     *
     * @param error the error
     */
    public abstract void onSetFail(Error error);

    @Override
    public void onSuccess(JSONObject result) {
        try {
            JSONArray resultJSONArray = result.getJSONArray("result");
            int resultCount = resultJSONArray.length();
            Role[] roles = new Role[resultCount];

            for (int idx = 0; idx < resultCount; idx++) {
                String perRoleName = resultJSONArray.getString(idx);
                roles[idx] = new Role(perRoleName);
            }

            this.onSetSuccess(roles);
        } catch (JSONException e) {
            this.onSetFail(new Error("Malformed server response"));
        }
    }

    @Override
    public void onFail(Error error) {
        this.onSetFail(error);
    }
}
