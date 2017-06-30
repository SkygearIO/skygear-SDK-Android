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
 * The Skygear User Query Response Handler.
 */
public abstract class UserQueryResponseHandler implements ResponseHandler {
    /**
     * Query success callback.
     *
     * @param users the users
     */
    public abstract void onQuerySuccess(User[] users);

    /**
     * Query fail callback.
     *
     * @param error the error
     */
    public abstract void onQueryFail(Error error);

    @Override
    public void onSuccess(JSONObject result) {
        try {
            JSONArray data = result.getJSONArray("result");
            int count = data.length();
            User[] users = new User[count];

            for (int idx = 0; idx < count; idx++) {
                JSONObject perUserData = data.getJSONObject(idx).getJSONObject("data");
                User perUser = UserSerializer.deserialize(perUserData);

                users[idx] = perUser;
            }

            this.onQuerySuccess(users);
        } catch (JSONException e) {
            this.onQueryFail(new Error("Malformed server response"));
        }
    }

    @Override
    public void onFail(Error error) {
        this.onQueryFail(error);
    }
}
