package io.skygear.skygear;

import org.json.JSONObject;

/**
 * The Set Disable Skygear User Response Handler.
 */
public abstract class SetDisableUserResponseHandler implements ResponseHandler {
    /**
     * Disable success callback.
     */
    public abstract void onSetSuccess();

    /**
     * Disable fail callback.
     *
     * @param error the error
     */
    public abstract void onSetFail(Error error);


    @Override
    public void onSuccess(JSONObject result) {
        this.onSetSuccess();
    }

    @Override
    public void onFail(Error error) {
        this.onSetFail(error);
    }
}
