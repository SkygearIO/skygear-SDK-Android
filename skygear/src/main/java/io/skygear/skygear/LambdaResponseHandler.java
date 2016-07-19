package io.skygear.skygear;

import org.json.JSONObject;

/**
 * The Skygear Lambda Response Handler.
 */
public abstract class LambdaResponseHandler implements Request.ResponseHandler {
    /**
     * The success callback.
     *
     * @param result the result
     */
    public abstract void onLambdaSuccess(JSONObject result);

    /**
     * The fail callback.
     *
     * @param reason the reason
     */
    public abstract void onLambdaFail(String reason);

    @Override
    public void onSuccess(JSONObject result) {
        this.onLambdaSuccess(result);
    }

    @Override
    public void onFail(Request.Error error) {
        this.onLambdaFail(error.getMessage());
    }
}
