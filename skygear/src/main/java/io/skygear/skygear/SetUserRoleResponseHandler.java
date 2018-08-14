package io.skygear.skygear;

import org.json.JSONObject;

/**
 * The Set Skygear User Role Response Handler.
 */
public abstract class SetUserRoleResponseHandler extends ResponseHandler {
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
    public final void onSuccess(JSONObject result) {
        this.onSetSuccess();
    }

    @Override
    public final void onFailure(Error error) {
        this.onSetFail(error);
    }
}
