package io.skygear.skygear;

import org.json.JSONObject;

/**
 * The Set Disable Skygear User Response Handler.
 */
public abstract class SetDisableUserResponseHandler extends ResponseHandler {
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
    public final void onSuccess(JSONObject result) {
        this.onSetSuccess();
    }

    @Override
    public final void onFail(Error error) {
        this.onSetFail(error);
    }
}
