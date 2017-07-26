package io.skygear.skygear;

import java.util.HashMap;

/**
 * The Get Skygear User Role Request.
 */
public class GetUserRoleRequest extends Request {
    /**
     * Instantiates a new Get User Role Request.
     *
     * @param userIDs the user id array
     */
    public GetUserRoleRequest(String[] userIDs) {
        super("role:get");
        this.data = new HashMap<>();
        this.data.put("users", userIDs);
    }
}
