package io.skygear.skygear;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The Get Skygear User Role Response Handler.
 */
public abstract class GetUserRoleResponseHandler implements ResponseHandler {
    /**
     * Get success callback.
     *
     * @param userRoles a user-to-roles mapping
     */
    public abstract void onGetSuccess(Map<String, Role[]> userRoles);

    /**
     * Get fail callback.
     *
     * @param error the error
     */
    public abstract void onGetFail(Error error);

    private Map<String, Role[]> parseUserRoles(JSONObject result) throws JSONException {
        Map<String, Role[]> userRoles = new HashMap<>();
        Iterator<String> keys = result.keys();
        while (keys.hasNext()) {
            String userID = keys.next();
            JSONArray roleNames = result.getJSONArray(userID);
            int length = roleNames.length();

            Role[] roles = new Role[length];
            for (int idx = 0; idx < length; idx++) {
                roles[idx] = new Role(roleNames.getString(idx));
            }

            userRoles.put(userID, roles);
        }
        return userRoles;
    }

    @Override
    public void onSuccess(JSONObject result) {
        try {
            Map<String, Role[]> userRoles = this.parseUserRoles(result.getJSONObject("result"));
            this.onGetSuccess(userRoles);
        } catch (JSONException e) {
            this.onGetFail(new Error("Malformed server response"));
        }
    }

    @Override
    public void onFail(Error error) {
        this.onGetFail(error);
    }
}
