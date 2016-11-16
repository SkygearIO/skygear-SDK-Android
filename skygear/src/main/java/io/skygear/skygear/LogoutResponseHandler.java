package io.skygear.skygear;

import org.json.JSONObject;

/**
 * The type Logout response handler.
 */
public abstract class LogoutResponseHandler implements ResponseHandler {
    /**
     * On logout success.
     */
    public abstract void onLogoutSuccess();

    /**
     * On logout fail.
     *
     * @param error the error
     */
    public abstract void onLogoutFail(Error error);

    @Override
    public void onSuccess(JSONObject result) {
        this.onLogoutSuccess();
    }

    @Override
    public void onFail(Error error) {
        this.onLogoutFail(error);
    }
}
