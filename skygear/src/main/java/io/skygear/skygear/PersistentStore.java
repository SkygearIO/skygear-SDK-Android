/*
 * Copyright 2017 Oursky Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
    private static final String TAG = "Skygear SDK";

    static final String SKYGEAR_PREF_SPACE = "SkygearSharedPreferences";

    static final String CURRENT_USER_KEY = "current_user";
    static final String ACCESS_TOKEN_KEY = "access_token";
    static final String DEFAULT_ACCESS_CONTROL_KEY = "default_access_control";
    static final String DEVICE_ID_KEY = "device_id";
    static final String DEVICE_TOKEN_KEY = "device_token";

    private final Context context;
    /**
     * The Current user.
     */
    Record currentUser;

    /**
     * The access token
     */
    String accessToken;

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
        this.restoreAccessToken(pref);
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
        this.saveAccessToken(prefEditor);
        this.saveDefaultAccessControl(prefEditor);
        this.saveDeviceId(prefEditor);
        this.saveDeviceToken(prefEditor);

        prefEditor.apply();
    }

    void restoreAuthUser(SharedPreferences pref) {
        String currentUserString = pref.getString(CURRENT_USER_KEY, null);

        if (currentUserString == null) {
            this.currentUser = null;
        } else {
            try {
                this.currentUser = RecordSerializer.deserialize(
                        new JSONObject(currentUserString)
                );
            } catch (JSONException e) {
                Log.w(TAG, "Fail to decode saved current user object");
                this.currentUser = null;
            }
        }
    }

    void saveAuthUser(SharedPreferences.Editor prefEditor) {
        if (this.currentUser != null) {
            prefEditor.putString(CURRENT_USER_KEY,
                    RecordSerializer.serialize(this.currentUser).toString()
            );
        } else {
            prefEditor.remove(CURRENT_USER_KEY);
        }
    }

    void restoreAccessToken(SharedPreferences pref) {
        this.accessToken = pref.getString(ACCESS_TOKEN_KEY, null);
    }

    void saveAccessToken(SharedPreferences.Editor prefEditor) {
        if (this.accessToken != null) {
            prefEditor.putString(ACCESS_TOKEN_KEY, this.accessToken);
        } else {
            prefEditor.remove(ACCESS_TOKEN_KEY);
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
            Log.w(TAG, "Fail to decode saved default access control");
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
