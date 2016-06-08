package io.skygear.skygear;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * The Skygear request manager.
 */
public class RequestManager {
    /**
     * The Request Queue.
     */
    RequestQueue queue;
    /**
     * The Endpoint.
     */
    String endpoint;
    /**
     * The Api key.
     */
    String apiKey;

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
        this.queue = Volley.newRequestQueue(context);
        this.configure(config);
    }

    /**
     * Updates the configuration of the Request manager.
     *
     * @param config the config
     */
    public void configure(Configuration config) {
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
        try {
            request.validate();
        } catch (Exception e) {
            request.onValidationError(e);
            return;
        }

        String action = request.action;
        String url = this.endpoint + action.replace(":", "/");

        Map<String, Object> data = request.data;
        data.putAll(this.getExtraData(action));

        JsonObjectRequest jsonRequest = new JsonRequest(
                url,
                new JSONObject(data),
                this.getExtraHeaders(),
                request,
                request
        );

        this.queue.add(jsonRequest);
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
            return this.extraHeaders;
        }

        @Override
        public String getBodyContentType() {
            return "application/json";
        }
    }
}
