package io.skygear.skygear;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Skygear User Save Response Handler.
 */
public abstract class UserSaveResponseHandler implements Request.ResponseHandler {
    /**
     * Save success callback.
     *
     * @param user the saved user
     */
    public abstract void onSaveSuccess(User user);

    /**
     * Save fail callback.
     *
     * @param reason the reason
     */
    public abstract void onSaveFail(String reason);

    @Override
    public void onSuccess(JSONObject result) {
        try {
            this.onSaveSuccess(UserSerializer.deserialize(result));
        } catch (JSONException e) {
            this.onSaveFail("Malformed server response");
        }
    }

    @Override
    public void onFail(Request.Error error) {
        this.onSaveFail(error.getMessage());
    }
}
