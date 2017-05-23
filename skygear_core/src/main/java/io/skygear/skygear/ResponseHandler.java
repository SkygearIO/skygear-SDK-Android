package io.skygear.skygear;

import org.json.JSONObject;

/**
 * The Response Handler interface for Skygear Request.
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
