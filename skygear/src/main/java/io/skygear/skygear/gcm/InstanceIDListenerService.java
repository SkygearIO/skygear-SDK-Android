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

import android.content.Intent;
import android.util.Log;

/**
 * The GCM Instance ID Listener Service.
 * <p>
 *     This Service will trigger an intent to {@link RegistrationIntentService}.
 * </p>
 */
public class InstanceIDListenerService extends com.google.android.gms.iid.InstanceIDListenerService {
    private static final String TAG = "Skygear SDK";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        Log.i(TAG, "Requesting to refresh token");
        this.startService(new Intent(this, RegistrationIntentService.class));
    }
}
