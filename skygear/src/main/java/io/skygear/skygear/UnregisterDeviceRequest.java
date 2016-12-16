package io.skygear.skygear;

import java.util.HashMap;

public class UnregisterDeviceRequest extends Request {
    public UnregisterDeviceRequest() {
        super("device:unregister");

        this.data = new HashMap<>();
    }

    public UnregisterDeviceRequest(String deviceId) {
        this();

        if (deviceId != null) {
            this.data.put("id", deviceId);
        }
    }
}
