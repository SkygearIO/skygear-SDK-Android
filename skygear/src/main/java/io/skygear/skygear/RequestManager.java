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

import android.content.Context;
import android.util.Log;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import io.skygear.utils.volley.SimpleMultiPartRequest;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The Skygear request manager.
 */
public class RequestManager {
    /** The default request timeout in milliseconds */
    public static final int DEFAULT_TIMEOUT = DefaultRetryPolicy.DEFAULT_TIMEOUT_MS;
    private static final String TAG = "Skygear SDK";

    /**
     * The Request Queue.
     */
    RequestQueue queue;
    /**
     * The Context.
     */
    Context context;
    /**
     * The Endpoint.
     */
    String endpoint;
    /**
     * The Api key.
     */
    String apiKey;

    /**
     * The Request Timeout (in milliseconds).
     */
    int requestTimeout;

    /**
     * The Access token.
     */
    public String accessToken;

    /**
     * Instantiates a new Request manager.
     *
     * @param context the context
     * @param config  the config
     */
    public RequestManager(Context context, Configuration config) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
        this.requestTimeout = DEFAULT_TIMEOUT;
        if (config != null) {
            this.configure(config);
        }
    }

    /**
     * Updates the configuration of the Request manager.
     *
     * @param config the config
     */
    public void configure(Configuration config) {
        if (config == null) {
            throw new InvalidParameterException("Null configuration is not allowed");
        }

        this.endpoint = config.endpoint;
        this.apiKey = config.apiKey;
    }

    /**
     * Gets extra data map based on the configuration.
     *
     * @param action the action
     * @return the extra data
     */
    Map<String, Object> getExtraData(String action) {
        Map<String, Object> extra = new HashMap<>();

        extra.put("action", action);
        extra.put("api_key", this.apiKey);

        if (this.accessToken != null && this.accessToken.length() > 0) {
            extra.put("access_token", this.accessToken);
        }

        return extra;
    }

    /**
     * Get extra headers map based on the configuration.
     *
     * @return the map
     */
    Map<String, String> getExtraHeaders(){
        Map<String, String> extra = new HashMap<>();

        extra.put("X-Skygear-API-Key", this.apiKey);
        extra.put("X-Skygear-SDK-Version", "skygear-SDK-Android/" + BuildConfig.VERSION_NAME);

        if (this.accessToken != null && this.accessToken.length() > 0) {
            extra.put("X-Skygear-Access-Token", this.accessToken);
        }

        return extra;
    }

    /**
     * Send a request.
     *
     * @param request the request
     */
    public void sendRequest(final Request request) {
        if (this.endpoint == null) {
            throw new IllegalStateException("Endpoint is not configured.");
        }

        try {
            request.validate();
        } catch (Exception e) {
            request.onValidationError(e);
            return;
        }

        String action = request.action;
        String url = this.endpoint + action.replace(":", "/");

        Map<String, Object> data = new HashMap<>(request.data);
        data.putAll(this.getExtraData(action));

        JsonObjectRequest jsonRequest = new JsonRequest(
                url,
                new JSONObject(data),
                this.getExtraHeaders(),
                request,
                request
        );

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                this.requestTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        this.queue.add(jsonRequest);
    }

    public void sendAssetPostRequest(final AssetPostRequest request) {
        if (this.endpoint == null) {
            throw new IllegalStateException("Endpoint is not configured.");
        }

        try {
            request.validate();
        } catch (Exception e) {
            request.onValidationError(e);
            return;
        }

        // write the asset data to a temp file
        File tempFile;
        try {
            tempFile = File.createTempFile("Skygear", null);

            FileOutputStream tempFileStream = new FileOutputStream(tempFile);
            tempFileStream.write(request.getAsset().data);
            tempFileStream.close();
        } catch (IOException e) {
            Log.e(TAG, "Fail to create temporary file", e);
            request.onValidationError(e);
            return;
        }

        // support relative URL
        URI uri;
        try {
            uri = new URI(request.getAction());
        } catch (URISyntaxException e) {
            Log.e(TAG, "Got malformed URL", e);
            request.onValidationError(e);
            return;
        }

        String uriString = uri.toString();
        if (!uri.isAbsolute()) {
            uriString = this.endpoint + uriString.substring(1);
        }

        // make multipart request
        MultiPartRequest multiPartRequest = new MultiPartRequest(
                uriString,
                this.getExtraHeaders(),
                request,
                request
        );

        Map<String, String> extraFields = request.getExtraFields();
        Set<String> extraFieldKeys = extraFields.keySet();
        for (String perKey : extraFieldKeys) {
            String perValue = extraFields.get(perKey);
            multiPartRequest.addStringParam(perKey, perValue);
        }

        multiPartRequest.addFile("file", tempFile.getAbsolutePath());
        this.queue.add(multiPartRequest);
    }

    private static class MultiPartRequest extends SimpleMultiPartRequest {
        private Map<String, String> extraHeaders;

        public MultiPartRequest(
                String url,
                Map<String, String> extraHeaders,
                Response.Listener<String> listener,
                Response.ErrorListener errorListener
        ) {
            super(url, listener, errorListener);

            this.extraHeaders = extraHeaders;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = new HashMap<>(super.getHeaders());
            headers.putAll(this.extraHeaders);

            return headers;
        }

        @Override
        public boolean isFixedStreamingMode() {
            return true;
        }
    }

    private static class JsonRequest extends JsonObjectRequest {
        private Map<String, String> extraHeaders;

        private JsonRequest(
                String url,
                JSONObject data,
                Map<String, String> extraHeaders,
                Response.Listener<JSONObject> listener,
                Response.ErrorListener errorListener
        ) {
            super(
                    com.android.volley.Request.Method.POST,
                    url,
                    data,
                    listener,
                    errorListener
            );

            this.extraHeaders = extraHeaders;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = new HashMap<>(super.getHeaders());
            headers.putAll(this.extraHeaders);

            return headers;
        }

        @Override
        public String getBodyContentType() {
            return "application/json";
        }
    }
}
