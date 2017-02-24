package io.skygear.skygear;

import java.util.HashMap;

/**
 * The Skygear Register Device Request.
 */
public class RegisterDeviceRequest extends Request {
    /**
     * Instantiates a new Skygear Register Device Request.
     */
    public RegisterDeviceRequest() {
        super("device:register");

        this.data = new HashMap<>();
        this.data.put("type", "android");
    }

    /**
     * Instantiates a new Skygear Register Device Request with
     * Device ID and Device Token
     *
     * @deprecated use {@link #RegisterDeviceRequest(String, String, String)} instead.
     *
     * @param deviceId    the device id
     * @param deviceToken the device token
     */
    @Deprecated
    public RegisterDeviceRequest(String deviceId, String deviceToken) {
        this();

        if (deviceId != null) {
            this.data.put("id", deviceId);
        }

        if (deviceToken != null) {
            this.data.put("device_token", deviceToken);
        }
    }

    /**
     * Instantiates a new Skygear Register Device Request with
     * Device ID, Device Token and Package Name
     *
     * @param deviceId    the device id
     * @param deviceToken the device token
     * @param topic       the topic, should equal to the package name of the application
     */
    public RegisterDeviceRequest(String deviceId, String deviceToken, String topic) {
        this();

        if (deviceId != null) {
            this.data.put("id", deviceId);
        }

        if (deviceToken != null) {
            this.data.put("device_token", deviceToken);
        }

        if (topic != null) {
            this.data.put("topic", topic);
        }
    }
}
