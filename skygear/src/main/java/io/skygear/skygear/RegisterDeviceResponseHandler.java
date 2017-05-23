package io.skygear.skygear;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Skygear Register Device Response Handler.
 */
public abstract class RegisterDeviceResponseHandler implements ResponseHandler {

    /**
     * Register success callback.
     *
     * @param deviceId the device id
     */
    public abstract void onRegisterSuccess(String deviceId);

    /**
     * Register error callback.
     *
     * @param error the error
     */
    public abstract void onRegisterError(Error error);

    @Override
    public void onSuccess(JSONObject result) {
        try {
            String deviceId = result.getString("id");
            this.onRegisterSuccess(deviceId);
        } catch (JSONException e) {
            this.onRegisterError(new Error("Malformed server response"));
        }
    }

    @Override
    public void onFail(Error error) {
        this.onRegisterError(error);
    }
}
