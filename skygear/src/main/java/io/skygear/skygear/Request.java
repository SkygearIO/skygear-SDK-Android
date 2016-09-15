package io.skygear.skygear;

import com.android.volley.error.VolleyError;
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
            String errorString;
            if (error.networkResponse != null && error.networkResponse.data != null) {
                String networkErrorString = new String(error.networkResponse.data);
                try {
                    JSONObject errorObject = new JSONObject(networkErrorString);
                    errorString = errorObject.getJSONObject("error").getString("message");
                } catch (JSONException e) {
                    errorString = networkErrorString;
                }
            } else {
                errorString = error.getMessage();
            }

            this.responseHandler.onFail(new Request.Error(errorString));
        }
    }

    /**
     * The interface Response handler for Skygear Request.
     */
    public interface ResponseHandler {
        /**
         * The success callback.
         *
         * @param result the result
         */
        void onSuccess(JSONObject result);

        /**
         * The error callback.
         *
         * @param error the error
         */
        void onFail(Error error);
    }

    /**
     * The Error on error callback of Response handler.
     */
    public static class Error extends Exception {
        /**
         * Instantiates a new Error.
         *
         * @param detailMessage the detail message
         */
        public Error(String detailMessage) {
            super(detailMessage);
        }
    }
}
