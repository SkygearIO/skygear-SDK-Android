package io.skygear.skygear;


import java.util.HashMap;

/**
 * The Skygear Get Current User Request.
 */
public class GetCurrentUserRequest extends Request {
    /**
     * Instantiates a new Skygear Get Current User Request.
     */
    public GetCurrentUserRequest() {
        super("me", new HashMap<String, Object>(), null);
    }
}
