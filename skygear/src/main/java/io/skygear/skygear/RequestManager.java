package io.skygear.skygear;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
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
        String action = request.action;
        String url = this.endpoint + action.replace(":", "/");

        Map<String, Object> data = request.data;
        data.putAll(this.getExtraData(action));

        final Map<String, String> extraHeaders = this.getExtraHeaders();
        JsonObjectRequest volleyRequest = new JsonObjectRequest(
                com.android.volley.Request.Method.POST,
                url,
                new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (request.responseHandler != null) {
                            request.responseHandler.onSuccess(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (request.responseHandler != null) {
                            String errorString;
                            if (error.networkResponse != null) {
                                errorString = new String(error.networkResponse.data);
                            } else {
                                errorString = error.getMessage();
                            }

                            request.responseHandler.onFail(new Request.Error(errorString));
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return extraHeaders;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        this.queue.add(volleyRequest);
    }
}
