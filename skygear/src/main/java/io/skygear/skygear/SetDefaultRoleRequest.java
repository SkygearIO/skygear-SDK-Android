package io.skygear.skygear;

import java.util.HashMap;

/**
 * The Skygear Default Role Setup Request.
 */
public class SetDefaultRoleRequest extends Request {
    /**
     * Instantiates a new Set Default Role Request.
     *
     * @param roles the roles array
     */
    public SetDefaultRoleRequest(Role[] roles) {
        super("role:default");

        this.data = new HashMap<>();

        String[] roleNames = new String[roles.length];
        for (int idx = 0; idx < roles.length; idx++) {
            roleNames[idx] = roles[idx].getName();
        }

        this.data.put("roles", roleNames);
    }
}
