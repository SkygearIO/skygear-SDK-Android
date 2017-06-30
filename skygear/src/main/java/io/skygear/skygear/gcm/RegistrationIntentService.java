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

package io.skygear.skygear.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import io.skygear.skygear.Container;


/**
 * The GCM Device Registration Intent Service.
 */
public class RegistrationIntentService extends IntentService {
    private static final String TAG = "Skygear SDK";

    /**
     * Instantiates a new Registration Intent Service with specific name.
     *
     * @param name the name
     */
    public RegistrationIntentService(String name) {
        super(name);
    }

    /**
     * Instantiates a new Registration Intent Service.
     */
    public RegistrationIntentService() {
        this(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Container container = Container.defaultContainer(this.getApplicationContext());
        String gcmSenderId = container.getPush().getGcmSenderId();

        if (gcmSenderId != null) {
            try {
                String token = InstanceID.getInstance(this).getToken(
                        gcmSenderId,
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE
                );
                Log.i(TAG, "Successfully get device token = " + token);

                container.getPush().registerDeviceToken(token);
            } catch (IOException e) {
                Log.w(TAG, String.format("Fail to get GCM device token: %s", e.getMessage()), e);
            }
        } else {
            Log.w(TAG, "GCM Sender ID should not be null");
        }
    }
}
