package io.skygear.skygear;

import org.json.JSONObject;

/**
 * The type Logout response handler.
 */
public abstract class LogoutResponseHandler implements Request.ResponseHandler {
    /**
     * On logout success.
     */
    public abstract void onLogoutSuccess();

    /**
     * On logout fail.
     *
     * @param reason the reason
     */
    public abstract void onLogoutFail(String reason);

    @Override
    public void onSuccess(JSONObject result) {
        this.onLogoutSuccess();
    }

    @Override
    public void onFail(Request.Error error) {
        this.onLogoutFail(error.getMessage());
    }
}
