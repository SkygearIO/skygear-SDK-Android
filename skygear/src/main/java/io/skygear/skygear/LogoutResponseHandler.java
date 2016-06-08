package io.skygear.skygear;

import org.json.JSONObject;

/**
 * The type Logout response handler.
 */
public abstract class LogoutResponseHandler implements Request.ResponseHandler {
    /**
     * The Auth resolver.
     */
    public AuthResolver authResolver;

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
        if (this.authResolver != null) {
            this.authResolver.resolveAuthToken(null);
        }

        this.onLogoutSuccess();
    }

    @Override
    public void onFail(Request.Error error) {
        this.onLogoutFail(error.getMessage());
    }
}
