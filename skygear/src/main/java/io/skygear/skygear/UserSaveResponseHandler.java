package io.skygear.skygear;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Skygear User Save Response Handler.
 */
public abstract class UserSaveResponseHandler implements ResponseHandler {
    /**
     * Save success callback.
     *
     * @param user the saved user
     */
    public abstract void onSaveSuccess(User user);

    /**
     * Save fail callback.
     *
     * @param error the error
     */
    public abstract void onSaveFail(Error error);

    @Override
    public void onSuccess(JSONObject result) {
        try {
            this.onSaveSuccess(UserSerializer.deserialize(result));
        } catch (JSONException e) {
            this.onSaveFail(new Error("Malformed server response"));
        }
    }

    @Override
    public void onFail(Error error) {
        this.onSaveFail(error);
    }
}
