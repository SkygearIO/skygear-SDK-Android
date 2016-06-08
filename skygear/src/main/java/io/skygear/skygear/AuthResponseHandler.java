package io.skygear.skygear;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Auth response handler.
 */
public abstract class AuthResponseHandler implements Request.ResponseHandler {
    /**
     * The Auth resolver.
     */
    public AuthResolver authResolver;

    /**
     * Auth success callback
     *
     * @param token access token
     */
    public abstract void onAuthSuccess(String token);

    /**
     * Auth fail callback
     *
     * @param reason access reason
     */
    public abstract void onAuthFail(String reason);

    @Override
    public void onSuccess(JSONObject result) {
        try {
            String accessToken = result.getString("access_token");
            if (this.authResolver != null) {
                this.authResolver.resolveAuthToken(accessToken);
            }
            this.onAuthSuccess(accessToken);
        } catch (JSONException e) {
            this.onAuthFail("Malformed server response");
        }
    }

    @Override
    public void onFail(Request.Error error) {
        this.onAuthFail(error.getMessage());
    }
}
