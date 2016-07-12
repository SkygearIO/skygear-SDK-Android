package io.skygear.skygear;

import java.util.HashMap;

/**
 * The Skygear Admin Role Setup Request.
 */
public class SetAdminRoleRequest extends Request {
    /**
     * Instantiates a new Set Admin Role Request.
     *
     * @param roles the roles array
     */
    public SetAdminRoleRequest(Role[] roles) {
        super("role:admin");

        this.data = new HashMap<>();

        String[] roleNames = new String[roles.length];
        for (int idx = 0; idx < roles.length; idx++) {
            roleNames[idx] = roles[idx].getName();
        }

        this.data.put("roles", roleNames);
    }
}
