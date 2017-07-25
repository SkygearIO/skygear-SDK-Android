package io.skygear.skygear;

import java.security.InvalidParameterException;
import java.util.HashMap;

/**
 * The Get Skygear User Role Request.
 */
public class GetUserRoleRequest extends Request {
    /**
     * Instantiates a new Get User Role Request.
     *
     * @param users the users array
     */
    public GetUserRoleRequest(Record[] users) {
        super("role:get");

        this.validateUserRecords(users);

        this.data = new HashMap<>();

        String[] userIDs = new String[users.length];
        for (int idx = 0; idx < users.length; idx++) {
            userIDs[idx] = users[idx].getId();
        }

        this.data.put("users", userIDs);
    }

    private void validateUserRecords(Record[] users) {
        for (Record user : users) {
            if (!user.getType().equals("user")) {
                throw new InvalidParameterException("Record type should be user");
            }
        }
    }
}
