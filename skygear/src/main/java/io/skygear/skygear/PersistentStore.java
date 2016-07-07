package io.skygear.skygear;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
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
     * The Default Access Control.
     */
    AccessControl defaultAccessControl;

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
     * Restore saved properties
     */
    void restore() {
        this.restoreAuthUser();
        this.restoreDefaultAccessControl();
    }

    /**
     * Save properties to persistent store.
     */
    void save() {
        this.saveAuthUser();
        this.saveDefaultAccessControl();
    }


    static final String CURRENT_USER_KEY = "current_user";
    private void restoreAuthUser() {
        SharedPreferences pref = context.getSharedPreferences(SKYGEAR_PREF_SPACE, Context.MODE_PRIVATE);
        String currentUserString = pref.getString(CURRENT_USER_KEY, "{}");

        try {
            this.currentUser = UserSerializer.deserialize(
                    new JSONObject(currentUserString)
            );
        } catch (JSONException e) {
            Log.w("Skygear SDK", "Fail to decode saved current user object");
            this.currentUser = null;
        }
    }

    private void saveAuthUser() {
        SharedPreferences pref = context.getSharedPreferences(SKYGEAR_PREF_SPACE, Context.MODE_PRIVATE);
        SharedPreferences.Editor authUserEditor = pref.edit();

        if (this.currentUser != null) {
            authUserEditor.putString(CURRENT_USER_KEY,
                    UserSerializer.serialize(this.currentUser).toString()
            );
        } else {
            authUserEditor.remove(CURRENT_USER_KEY);
        }

        authUserEditor.apply();
    }

    static final String DEFAULT_ACCESS_CONTROL_KEY = "default_access_control";
    private void restoreDefaultAccessControl() {
        SharedPreferences pref = context.getSharedPreferences(SKYGEAR_PREF_SPACE, Context.MODE_PRIVATE);
        String defaultAccessControlString = pref.getString(
                DEFAULT_ACCESS_CONTROL_KEY,
                "[{\"public\": true, \"level\": \"read\"}]"
        );

        try {
            JSONArray defaultAccessControlObject = new JSONArray(defaultAccessControlString);
            this.defaultAccessControl
                    = AccessControlSerializer.deserialize(defaultAccessControlObject);
        } catch (JSONException e) {
            Log.w("Skygear SDK", "Fail to decode saved default access control");
            this.defaultAccessControl = null;
        }
    }

    private void saveDefaultAccessControl() {
        SharedPreferences pref = context.getSharedPreferences(SKYGEAR_PREF_SPACE, Context.MODE_PRIVATE);
        SharedPreferences.Editor defaultAccessControlEditor = pref.edit();

        if (this.defaultAccessControl != null) {
            defaultAccessControlEditor.putString(
                    DEFAULT_ACCESS_CONTROL_KEY,
                    AccessControlSerializer.serialize(this.defaultAccessControl).toString()
            );
        } else {
            defaultAccessControlEditor.remove(DEFAULT_ACCESS_CONTROL_KEY);
        }

        defaultAccessControlEditor.apply();
    }
}
