package io.skygear.skygear;

import org.json.JSONObject;

/**
 * The Skygear Lambda Response Handler.
 */
public abstract class LambdaResponseHandler implements ResponseHandler {
    /**
     * The success callback.
     *
     * @param result the result
     */
    public abstract void onLambdaSuccess(JSONObject result);

    /**
     * The fail callback.
     *
     * @param error the error
     */
    public abstract void onLambdaFail(Error error);

    @Override
    public void onSuccess(JSONObject result) {
        this.onLambdaSuccess(result);
    }

    @Override
    public void onFail(Error error) {
        this.onLambdaFail(error);
    }
}
