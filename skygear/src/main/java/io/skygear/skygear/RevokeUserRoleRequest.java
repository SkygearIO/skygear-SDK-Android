package io.skygear.skygear;

import java.util.HashMap;

/**
 * The Revoke Skygear User Role Request.
 */
public class RevokeUserRoleRequest extends Request {
    /**
     * Instantiates a new Revoke User Role Request.
     *
     * @param userIDs   the user id array
     * @param roleNames the role name array
     */
    public RevokeUserRoleRequest(String[] userIDs, String []roleNames) {
        super("auth:role:revoke");
        this.data = new HashMap<>();
        this.data.put("users", userIDs);
        this.data.put("roles", roleNames);
    }
}
