package io.skygear.skygear;

import org.json.JSONObject;

/**
 * The Set Skygear User Role Response Handler.
 */
public abstract class SetUserRoleResponseHandler implements ResponseHandler {
    /**
     * Set success callback.
     */
    public abstract void onSetSuccess();

    /**
     * Set fail callback.
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
