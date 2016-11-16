package io.skygear.skygear;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Skygear Role Setup Response Handler.
 */
public abstract class SetRoleResponseHandler implements ResponseHandler {
    /**
     * Setup success callback.
     *
     * @param roles the roles being set
     */
    public abstract void onSetSuccess(Role[] roles);

    /**
     * Setup fail callback.
     *
     * @param error the error
     */
    public abstract void onSetFail(Error error);

    @Override
    public void onSuccess(JSONObject result) {
        try {
            JSONArray resultJSONArray = result.getJSONArray("result");
            int resultCount = resultJSONArray.length();
            Role[] roles = new Role[resultCount];

            for (int idx = 0; idx < resultCount; idx++) {
                String perRoleName = resultJSONArray.getString(idx);
                roles[idx] = new Role(perRoleName);
            }

            this.onSetSuccess(roles);
        } catch (JSONException e) {
            this.onSetFail(new Error("Malformed server response"));
        }
    }

    @Override
    public void onFail(Error error) {
        this.onSetFail(error);
    }
}
