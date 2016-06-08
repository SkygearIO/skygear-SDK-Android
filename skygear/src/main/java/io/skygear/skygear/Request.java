package io.skygear.skygear;

import org.json.JSONObject;

import java.util.Map;

/**
 * A Skygear Request.
 */
public class Request {
    /**
     * Request Action.
     * It will be in format of "namespace:action".
     */
    public final String action;
    /**
     * The Data.
     */
    public final Map<String, Object> data;
    /**
     * The Response handler.
     */
    public ResponseHandler responseHandler;

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
