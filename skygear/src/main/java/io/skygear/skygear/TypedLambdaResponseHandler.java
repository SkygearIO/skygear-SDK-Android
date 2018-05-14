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
 * The Skygear Typed Lambda Response Handler.
 */
public abstract class TypedLambdaResponseHandler<T extends Object> extends LambdaResponseHandler {
    /**
     * The success callback.
     *
     * @param result the result
     */
    public abstract void onLambdaSuccess(T result);

    public void onLambdaSuccess(JSONObject result) {
        this.onLambdaSuccess((T)result);
    }

    @Override
    public void onSuccess(JSONObject result) {
        try {
            // The object can be a JSONObject, JSONArray or other
            // JSON-compatible types.
            Object anyJSONObject = result.opt("result");
            this.onLambdaSuccess((T)ValueSerializer.deserialize(anyJSONObject));
        } catch (JSONException ex) {
            this.onFail(new Error(ex.getMessage()));
        } catch (ClassCastException ex) {
            this.onFail(new Error(ex.getMessage()));
        }
    }
}
