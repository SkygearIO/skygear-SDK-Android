package io.skygear.skygear;

import org.json.JSONObject;

/**
 * The Skygear PubsubHandler interface.
 */
public interface PubsubHandler {
    /**
     * The Handle Function.
     *
     * @param data the data
     */
    void handle(JSONObject data);
}
