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
        String gcmSenderId = container.push().getGcmSenderId();

        if (gcmSenderId != null) {
            try {
                String token = InstanceID.getInstance(this).getToken(
                        gcmSenderId,
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE
                );
                Log.i(TAG, "Successfully get device token = " + token);

                container.push().registerDeviceToken(token);
            } catch (IOException e) {
                Log.w(TAG, String.format("Fail to get GCM device token: %s", e.getMessage()), e);
            }
        } else {
            Log.w(TAG, "GCM Sender ID should not be null");
        }
    }
}
