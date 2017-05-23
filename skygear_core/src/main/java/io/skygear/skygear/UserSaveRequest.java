package io.skygear.skygear;

import java.util.HashMap;

/**
 * The Skygear User Save Request.
 */
public class UserSaveRequest extends Request {
    /**
     * Instantiates a new Skygear User Save Request.
     *
     * @param user the user
     */
    public UserSaveRequest(User user) {
        super("user:update");

        this.data = new HashMap<>();
        this.data.put("_id", user.getId());

        String email = user.getEmail();
        if (email != null) {
            this.data.put("email", email);
        }

        Role[] roles = user.getRoles();
        String[] roleNames = new String[roles.length];
        for (int idx = 0; idx < roles.length; idx++) {
            roleNames[idx] = roles[idx].getName();
        }

        this.data.put("roles", roleNames);
    }
}
