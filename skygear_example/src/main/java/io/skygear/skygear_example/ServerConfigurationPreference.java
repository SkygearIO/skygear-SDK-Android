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

package io.skygear.skygear_example;

import android.content.Context;
import android.content.SharedPreferences;

import io.skygear.skygear.Configuration;

public class ServerConfigurationPreference {
    private static final String PREF_SPACE = "SkygearExampleSharedPreference";
    private static final String SERVER_ENDPOINT_PREF_KEY = "SkygearExampleServerEndpointPreference";
    private static final String SERVER_API_KEY_PREF_KEY = "SkygearExampleServerApiKeyPreference";
    private static final String GCM_SENDER_ID_PREF_KEY = "SkygearExampleGcmSenderIdPreference";
    private static final String ENCRYPT_USER_DATA_PREF_KEY = "SkygearExampleEncryptUserDataPreferenece";

    private final Context context;

    public ServerConfigurationPreference(Context context) {
        super();

        this.context = context;
    }

    public void update(Configuration config) {
        SharedPreferences pref = this.context.getSharedPreferences(PREF_SPACE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        if (config == null) {
            editor.remove(SERVER_ENDPOINT_PREF_KEY);
            editor.remove(SERVER_API_KEY_PREF_KEY);
            editor.remove(GCM_SENDER_ID_PREF_KEY);
            editor.remove(ENCRYPT_USER_DATA_PREF_KEY);
        } else {
            editor.putString(SERVER_ENDPOINT_PREF_KEY, config.getEndpoint());
            editor.putString(SERVER_API_KEY_PREF_KEY, config.getApiKey());
            editor.putString(GCM_SENDER_ID_PREF_KEY, config.getGcmSenderId());
            editor.putBoolean(ENCRYPT_USER_DATA_PREF_KEY, config.encryptCurrentUserData());
        }

        editor.apply();
    }

    public Configuration get() {
        SharedPreferences pref = this.context.getSharedPreferences(PREF_SPACE, Context.MODE_PRIVATE);
        String endpoint = pref.getString(SERVER_ENDPOINT_PREF_KEY, null);
        String apiKey = pref.getString(SERVER_API_KEY_PREF_KEY, null);
        String gcmSenderId = pref.getString(GCM_SENDER_ID_PREF_KEY, null);
        boolean encryptUserData = pref.getBoolean(ENCRYPT_USER_DATA_PREF_KEY, false);

        if (endpoint == null || apiKey == null) {
            return null;
        }

        return new Configuration.Builder()
                .endPoint(endpoint)
                .apiKey(apiKey)
                .gcmSenderId(gcmSenderId)
                .encryptCurrentUserData(encryptUserData)
                .build();
    }
}
