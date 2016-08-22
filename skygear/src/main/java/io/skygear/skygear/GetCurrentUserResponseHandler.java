package io.skygear.skygear;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Skygear Get Current User Response Handler.
 */
public abstract class GetCurrentUserResponseHandler implements Request.ResponseHandler {

    /**
     * The success callback
     *
     * @param user the current user
     */
    public abstract void onGetCurrentUserSuccess(User user);

    /**
     * The fail callback
     *
     * @param reason the reason
     */
    public abstract void onGetCurrentUserFail(String reason);

    @Override
    public void onSuccess(JSONObject result) {
        try {
            this.onGetCurrentUserSuccess(UserSerializer.deserialize(result));
        } catch (JSONException e) {
            this.onGetCurrentUserFail("Malformed server response");
        }
    }

    @Override
    public void onFail(Request.Error error) {
        this.onGetCurrentUserFail(error.getMessage());
    }
}
