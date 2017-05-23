package io.skygear.skygear;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Skygear Unregister Device Response Handler.
 */
public abstract class UnregisterDeviceResponseHandler implements ResponseHandler {

    /**
     * Unregister success callback.
     *
     * @param deviceId the device id
     */
    public abstract void onUnregisterSuccess(String deviceId);

    /**
     * Unregister error callback.
     *
     * @param error the error
     */
    public abstract void onUnregisterError(Error error);

    @Override
    public void onSuccess(JSONObject result) {
        try {
            String deviceId = result.getString("id");
            this.onUnregisterSuccess(deviceId);
        } catch (JSONException e) {
            this.onUnregisterError(new Error("Malformed server response"));
        }
    }

    @Override
    public void onFail(Error error) {
        this.onUnregisterError(error);
    }
}
