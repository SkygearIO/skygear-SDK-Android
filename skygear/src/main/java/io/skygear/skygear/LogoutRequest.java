package io.skygear.skygear;

import java.util.HashMap;

/**
 * The Logout request.
 */
public class LogoutRequest extends Request {
    /**
     * Instantiates a new Logout request.
     */
    public LogoutRequest() {
        super("auth:logout", new HashMap<String, Object>());
    }
}
