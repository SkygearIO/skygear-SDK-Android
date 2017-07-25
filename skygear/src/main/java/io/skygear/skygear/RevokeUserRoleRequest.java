package io.skygear.skygear;

import java.security.InvalidParameterException;
import java.util.HashMap;

/**
 * The Revoke Skygear User Role Request.
 */
public class RevokeUserRoleRequest extends Request {
    /**
     * Instantiates a new Assign User Role Request.
     *
     * @param users the users array
     * @param roles the roles array
     */
    public RevokeUserRoleRequest(Record[] users, Role[] roles) {
        super("role:revoke");

        this.validateUserRecords(users);

        this.data = new HashMap<>();

        String[] userIDs = new String[users.length];
        for (int idx = 0; idx < users.length; idx++) {
            userIDs[idx] = users[idx].getId();
        }

        String[] roleNames = new String[roles.length];
        for (int idx = 0; idx < roles.length; idx++) {
            roleNames[idx] = roles[idx].getName();
        }

        this.data.put("users", userIDs);
        this.data.put("roles", roleNames);
    }

    private void validateUserRecords(Record[] users) {
        for (Record user : users) {
            if (!user.getType().equals("user")) {
                throw new InvalidParameterException("Record type should be user");
            }
        }
    }
}
