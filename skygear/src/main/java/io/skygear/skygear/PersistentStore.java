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

    static final String CURRENT_USER_KEY = "current_user";
    static final String DEFAULT_ACCESS_CONTROL_KEY = "default_access_control";
    static final String DEVICE_ID_KEY = "device_id";
    static final String DEVICE_TOKEN_KEY = "device_token";

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
     * The Device ID.
     */
    String deviceId;

    /**
     * The Device Token.
     */
    String deviceToken;

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
        SharedPreferences pref = this.context.getSharedPreferences(SKYGEAR_PREF_SPACE, Context.MODE_PRIVATE);

        this.restoreAuthUser(pref);
        this.restoreDefaultAccessControl(pref);
        this.restoreDeviceId(pref);
        this.restoreDeviceToken(pref);
    }

    /**
     * Save properties to persistent store.
     */
    void save() {
        SharedPreferences pref = this.context.getSharedPreferences(SKYGEAR_PREF_SPACE, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = pref.edit();

        this.saveAuthUser(prefEditor);
        this.saveDefaultAccessControl(prefEditor);
        this.saveDeviceId(prefEditor);
        this.saveDeviceToken(prefEditor);

        prefEditor.apply();
    }

    private void restoreAuthUser(SharedPreferences pref) {
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

    private void saveAuthUser(SharedPreferences.Editor prefEditor) {
        if (this.currentUser != null) {
            prefEditor.putString(CURRENT_USER_KEY,
                    UserSerializer.serialize(this.currentUser).toString()
            );
        } else {
            prefEditor.remove(CURRENT_USER_KEY);
        }
    }

    private void restoreDefaultAccessControl(SharedPreferences pref) {
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

    private void saveDefaultAccessControl(SharedPreferences.Editor prefEditor) {
        if (this.defaultAccessControl != null) {
            prefEditor.putString(
                    DEFAULT_ACCESS_CONTROL_KEY,
                    AccessControlSerializer.serialize(this.defaultAccessControl).toString()
            );
        } else {
            prefEditor.remove(DEFAULT_ACCESS_CONTROL_KEY);
        }
    }

    private void restoreDeviceId(SharedPreferences pref) {
        this.deviceId = pref.getString(DEVICE_ID_KEY, null);
    }

    private void saveDeviceId(SharedPreferences.Editor prefEditor) {
        if (this.deviceId == null) {
            prefEditor.remove(DEVICE_ID_KEY);
        } else {
            prefEditor.putString(DEVICE_ID_KEY, this.deviceId);
        }
    }

    private void restoreDeviceToken(SharedPreferences pref) {
        this.deviceToken = pref.getString(DEVICE_TOKEN_KEY, null);
    }

    private void saveDeviceToken(SharedPreferences.Editor prefEditor) {
        if (this.deviceToken == null) {
            prefEditor.remove(DEVICE_TOKEN_KEY);
        } else {
            prefEditor.putString(DEVICE_TOKEN_KEY, this.deviceToken);
        }
    }
}
