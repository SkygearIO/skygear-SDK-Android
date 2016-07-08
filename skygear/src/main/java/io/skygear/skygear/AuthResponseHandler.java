package io.skygear.skygear;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Auth response handler.
 */
public abstract class AuthResponseHandler implements Request.ResponseHandler {
    /**
     * Auth success callback
     *
     * @param user authenticated user
     */
    public abstract void onAuthSuccess(User user);

    /**
     * Auth fail callback
     *
     * @param reason access reason
     */
    public abstract void onAuthFail(String reason);

    @Override
    public void onSuccess(JSONObject result) {
        try {
            this.onAuthSuccess(UserSerializer.deserialize(result));
        } catch (JSONException e) {
            this.onAuthFail("Malformed server response");
        }
    }

    @Override
    public void onFail(Request.Error error) {
        this.onAuthFail(error.getMessage());
    }
}
