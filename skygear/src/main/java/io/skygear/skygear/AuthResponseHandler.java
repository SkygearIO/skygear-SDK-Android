package io.skygear.skygear;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Auth response handler.
 */
public abstract class AuthResponseHandler implements ResponseHandler {
    /**
     * Auth success callback
     *
     * @param user authenticated user
     */
    public abstract void onAuthSuccess(User user);

    /**
     * Auth fail callback
     *
     * @param error the error
     */
    public abstract void onAuthFail(Error error);

    @Override
    public void onSuccess(JSONObject result) {
        try {
            this.onAuthSuccess(UserSerializer.deserialize(result));
        } catch (JSONException e) {
            this.onAuthFail(new Error("Malformed server response"));
        }
    }

    @Override
    public void onFail(Error error) {
        this.onAuthFail(error);
    }
}
