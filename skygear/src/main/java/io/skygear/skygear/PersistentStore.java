package io.skygear.skygear;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Skygear persistent store.
 *
 * This class manages persistent data of Skygear.
 */
class PersistentStore {
    static final String SKYGEAR_PREF_SPACE = "SkygearSharedPreferences";

    private final Context context;
    /**
     * The Current user.
     */
    User currentUser;

    /**
     * Instantiates a new Persistent store.
     *
     * @param context the context
     */
    public PersistentStore(Context context) {
        super();

        this.context = context;
        this.restore();
    }

    /**
     * Restore from .
     */
    void restore() {
        this.restoreAuthUser();
    }

    /**
     * Save to persistent store.
     */
    void save() {
        this.saveAuthUser();
    }


    static final String CURRENT_USER_KEY = "current_user";
    private void restoreAuthUser() {
        SharedPreferences pref = context.getSharedPreferences(SKYGEAR_PREF_SPACE, Context.MODE_PRIVATE);
        String currentUserString = pref.getString(CURRENT_USER_KEY, "{}");

        try {
            JSONObject currentUserJson = new JSONObject(currentUserString);
            String userId = currentUserJson.getString("user_id");
            String accessToken = currentUserJson.getString("access_token");
            String username = null;
            String email = null;

            if (currentUserJson.has("username")) {
                username = currentUserJson.getString("username");
            }

            if (currentUserJson.has("email")) {
                email = currentUserJson.getString("email");
            }

            this.currentUser = new User(userId, accessToken, username, email);
        } catch (JSONException e) {
            Log.w("Skygear SDK", "Fail to decode saved current user object");
            this.currentUser = null;
        }
    }

    private void saveAuthUser() {
        SharedPreferences pref = context.getSharedPreferences(SKYGEAR_PREF_SPACE, Context.MODE_PRIVATE);
        SharedPreferences.Editor authUserEditor = pref.edit();

        JSONObject currentUserJson = new JSONObject();
        if (this.currentUser != null) {
            try {
                currentUserJson.put("user_id", this.currentUser.userId);
                currentUserJson.put("access_token", this.currentUser.accessToken);

                if (this.currentUser.username != null) {
                    currentUserJson.put("username", this.currentUser.username);
                }

                if (this.currentUser.email != null) {
                    currentUserJson.put("email", this.currentUser.email);
                }
            } catch (JSONException e) {
                Log.w("Skygear SDK", "Fail to encode current user object");
            }
        }

        authUserEditor.putString(CURRENT_USER_KEY, currentUserJson.toString());
        authUserEditor.apply();
    }
}
