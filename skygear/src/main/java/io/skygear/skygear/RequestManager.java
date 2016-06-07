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

public class RequestManager {
    RequestQueue queue;
    String endpoint;
    String apiKey;

    public String accessToken;

    public RequestManager(Context context, Configuration config) {
        this.queue = Volley.newRequestQueue(context);
        this.configure(config);
    }

    public void configure(Configuration config) {
        this.endpoint = config.endpoint;
        this.apiKey = config.apiKey;
    }

    Map<String, Object> getExtraData(String action) throws JSONException {
        Map<String, Object> extra = new HashMap<>();

        extra.put("action", action);
        extra.put("api_key", this.apiKey);

        if (this.accessToken != null && this.accessToken.length() > 0) {
            extra.put("access_token", this.accessToken);
        }

        return extra;
    }

    Map<String, String> getExtraHeaders(String action){
        Map<String, String> extra = new HashMap<>();

        extra.put("X-Skygear-API-Key", this.apiKey);

        if (this.accessToken != null && this.accessToken.length() > 0) {
            extra.put("X-Skygear-Access-Token", this.accessToken);
        }

        return extra;
    }

    public void sendRequest(final Request request) throws JSONException {
        String action = request.action;
        String url = this.endpoint + action.replace(":", "/");

        Map<String, Object> data = request.data;
        data.putAll(this.getExtraData(action));

        final Map<String, String> extraHeaders = this.getExtraHeaders(action);
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
                            String errorString = null;
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
