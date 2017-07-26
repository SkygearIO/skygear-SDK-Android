package io.skygear.skygear;

import java.util.HashMap;

/**
 * The Assign Skygear User Role Request.
 */
public class AssignUserRoleRequest extends Request {
    /**
     * Instantiates a new Assign User Role Request.
     *
     * @param userIDs   the user id array
     * @param roleNames the role name array
     */
    public AssignUserRoleRequest(String[] userIDs, String []roleNames) {
        super("role:assign");
        this.data = new HashMap<>();
        this.data.put("users", userIDs);
        this.data.put("roles", roleNames);
    }
}
