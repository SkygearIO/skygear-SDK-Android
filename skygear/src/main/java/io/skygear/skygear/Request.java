package io.skygear.skygear;

import com.android.volley.VolleyError;
import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

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
    Map<String, Object> data;
    /**
     * The Response handler.
     */
    public ResponseHandler responseHandler;

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
        this.responseHandler = responseHandler;
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
            this.responseHandler.onFail(new Error(exception.getMessage()));
        }
    }

    @Override
    public void onResponse(JSONObject response) {
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
                    String errorString = errorObject.getString("message");
                    int errorCodeValue = errorObject.getInt("code");

                    requestError = new Error(errorCodeValue, errorString);
                } catch (JSONException e) {
                    requestError = new Error(networkErrorString);
                }
            } else {
                requestError = new Error(error.getMessage());
            }

            this.responseHandler.onFail(requestError);
        }
    }

}
