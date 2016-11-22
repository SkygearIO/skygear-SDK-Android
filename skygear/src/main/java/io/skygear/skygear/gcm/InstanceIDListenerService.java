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
