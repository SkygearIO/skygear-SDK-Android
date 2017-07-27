package io.skygear.skygear;

import java.util.HashMap;

/**
 * The Fetch Skygear User Role Request.
 */
public class FetchUserRoleRequest extends Request {
    /**
     * Instantiates a new Fetch User Role Request.
     *
     * @param userIDs the user id array
     */
    public FetchUserRoleRequest(String[] userIDs) {
        super("role:get");
        this.data = new HashMap<>();
        this.data.put("users", userIDs);
    }
}
