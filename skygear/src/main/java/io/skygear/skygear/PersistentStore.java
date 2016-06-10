package io.skygear.skygear;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;

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
            this.currentUser = User.fromJsonString(currentUserString);
        } catch (JSONException e) {
            Log.w("Skygear SDK", "Fail to decode saved current user object");
            this.currentUser = null;
        }
    }

    private void saveAuthUser() {
        SharedPreferences pref = context.getSharedPreferences(SKYGEAR_PREF_SPACE, Context.MODE_PRIVATE);
        SharedPreferences.Editor authUserEditor = pref.edit();

        if (this.currentUser != null) {
            authUserEditor.putString(CURRENT_USER_KEY, this.currentUser.toJsonString());
        } else {
            authUserEditor.remove(CURRENT_USER_KEY);
        }

        authUserEditor.apply();
    }
}
