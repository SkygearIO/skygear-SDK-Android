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

import com.android.volley.VolleyError;
import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;

/**
 * A Skygear Request.
 */
public class Request implements Response.Listener<JSONObject>, Response.ErrorListener {
    /**
     * Request Action.
     * It will be in format of "namespace:action".
     */
    public final String action;
    /**
     * The Data.
     */
    protected Map<String, Object> data;

    private ResponseHandler responseHandler;

    /**
     * Instantiates a new Request.
     *
     * @param action the action
     */
    public Request(String action) {
        this(action, null, null);
    }

    /**
     * Instantiates a new Request.
     *
     * @param action the action
     * @param data   the data
     */
    public Request(String action, Map<String, Object>data) {
        this(action, data, null);
    }

    /**
     * Instantiates a new Request.
     *
     * @param action          the action
     * @param data            the data
     * @param responseHandler the response handler
     */
    public Request(String action, Map<String, Object>data, ResponseHandler responseHandler) {
        this.action = action;
        this.data = data;

        this.setResponseHandler(responseHandler);
    }

    public Map<String, Object> getData() {
        return Collections.unmodifiableMap(this.data);
    }

    /**
     * Set the Response Handler of the Request.
     *
     * After this method call, the response handler would have
     * a weak reference back to the request
     *
     * @param responseHandler the response handler of the request
     */
    public void setResponseHandler(ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;

        if (this.responseHandler != null) {
            this.responseHandler.requestRef = new WeakReference<>(this);
        }
    }

    public ResponseHandler getResponseHandler() {
        return this.responseHandler;
    }

    /**
     * Validation method.
     * This is be called before sending out the request.
     * Throw an exception to indicate any validation error.
     *
     * @throws Exception the exception
     */
    protected void validate() throws Exception {
        // Do nothing. Let subclasses to override it.
    }

    /**
     * Validation error callback
     *
     * @param exception the exception
     */
    public void onValidationError(Exception exception) {
        if (this.responseHandler != null) {
            this.responseHandler.onFailure(new Error(exception.getMessage()));
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        // TODO(cheungpat): It is not deisred to decide whether to unwrap the result
        // object depending on whether the `result` key contains a JSONObject
        // or not. In the future, the `onSuccess` method is always called
        // with a JSONObject containing a `result` key.

        // NOTE(cheungpat): LambdaRequest has overridden this method
        // with logic that does not implement conditional unwrapping. When
        // updating this method, consider also updating LambdaRequest.onResponse.
        if (this.responseHandler != null) {
            JSONObject resultObject;
            try {
                resultObject = response.getJSONObject("result");
            } catch (JSONException e) {
                resultObject = response;
            }

            this.responseHandler.onSuccess(resultObject);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (this.responseHandler != null) {
            Error requestError;
            if (error.networkResponse != null && error.networkResponse.data != null) {
                String networkErrorString = new String(error.networkResponse.data);
                try {
                    JSONObject errorObject = new JSONObject(networkErrorString).getJSONObject("error");
                    requestError = ErrorSerializer.deserialize(errorObject);
                } catch (JSONException e) {
                    requestError = new Error(networkErrorString);
                }
            } else {
                requestError = new Error(error.getMessage());
            }

            this.responseHandler.onFailure(requestError);
        }
    }

}
