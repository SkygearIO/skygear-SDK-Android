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

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.lang.ref.WeakReference;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Persister;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

/**
 * The Skygear Asset Post Request.
 */
public class AssetPostRequest implements Response.Listener<String>, Response.ErrorListener {
    /**
     * The Asset.
     */
    Asset asset;

    /**
     * The Post Action.
     */
    String action;
    /**
     * The Extra Fields of the Post Request.
     */
    Map<String, String> extraFields;

    private ResponseHandler responseHandler;

    /**
     * Instantiates a new Skygear Asset Post Request.
     *
     * @param asset       the asset
     * @param action      the action
     * @param extraFields the extra fields
     */
    public AssetPostRequest(Asset asset, String action, Map<String, String> extraFields) {
        super();

        this.asset = asset;
        this.action = action;
        this.extraFields = new HashMap<>();

        if (extraFields != null) {
            this.extraFields.putAll(extraFields);
        }
    }

    /**
     * Gets the asset.
     *
     * @return the asset
     */
    public Asset getAsset() {
        return asset;
    }

    /**
     * Gets the post action.
     *
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * Gets the extra fields.
     *
     * @return the extra fields
     */
    public Map<String, String> getExtraFields() {
        return extraFields;
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
     * Validate method.
     * This is be called before sending out the request.
     * Throw an exception to indicate any validation error.
     *
     * @throws Exception the exception
     */
    protected void validate() throws Exception {
        if (this.getAsset().getSize() <= 0) {
            throw new InvalidParameterException("Missing data for the asset");
        }
        if (this.getAsset().getMimeType().length() == 0) {
            throw new InvalidParameterException("Missing MIME type");
        }
    }

    private Error parseResponseError(VolleyError error) {
        S3Error s3Error = this.parseS3Error(error);
        if (s3Error != null) {
            return s3Error.toSkygearError();
        }

        return new Error(error.getMessage());
    }

    private S3Error parseS3Error(VolleyError error) {
        NetworkResponse response = error.networkResponse;
        InputStream inputStream = new ByteArrayInputStream(response.data);
        S3Error s3Error;
        try {
            s3Error = new Persister().read(S3Error.class, inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return s3Error;
    }

    /**
     * Validation error callback
     *
     * @param exception the exception
     */
    public void onValidationError(Exception exception) {
        if (this.responseHandler != null) {
            this.responseHandler.onPostFail(this.asset, new Error(exception.getMessage()));
        }
    }

    @Override
    public void onResponse(String response) {
        if (this.responseHandler != null) {
            this.responseHandler.onPostSuccess(this.asset, response);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (this.responseHandler != null) {
            this.responseHandler.onPostFail(this.asset, this.parseResponseError(error));
        }
    }

    /**
     * The Skygear Asset Post Response Handling interface.
     */
    public interface ResponseHandling {
        /**
         * Post success callback.
         *
         * @param asset    the asset
         * @param response the response
         */
        void onPostSuccess(Asset asset, String response);

        /**
         * Post fail callback.
         *
         * @param asset the asset
         * @param error the error
         */
        void onPostFail(Asset asset, Error error);
    }

    /**
     * The Skygear Asset Post Response Handler Base Class.
     */
    public static abstract class ResponseHandler implements ResponseHandling {
        WeakReference<AssetPostRequest> requestRef;

        /**
         * The request the handler is serving
         *
         * @return the request
         */
        public AssetPostRequest getRequest() {
            if (this.requestRef != null) {
                return this.requestRef.get();
            }

            return null;
        }
    }

    @Root(strict = false, name = "Error")
    private static class S3Error {
        @Element(name = "Code")
        String code;

        @Element(name = "Message")
        String message;

        Error toSkygearError() {
            if (this.code.equals("EntityTooLarge")) {
                return new Error(Error.Code.ASSET_SIZE_TOO_LARGE.getValue(), this.message);
            }

            return new Error(this.message);
        }
    }
}

