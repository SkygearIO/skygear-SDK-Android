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

import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Date;
import java.util.Random;

/**
 * The GCM Listener Service.
 */
public class ListenerService extends com.google.android.gms.gcm.GcmListenerService {
    private static final String TAG = "Skygear SDK";
    private final Random random;

    /**
     * Instantiates a new GCM listener service.
     */
    public ListenerService() {
        super();
        this.random = new Random(new Date().getTime());
    }

    @Override
    public void onMessageReceived(String s, Bundle bundle) {
        super.onMessageReceived(s, bundle);

        Bundle notification = bundle.getBundle("notification");
        if (notification == null) {
            Log.w(TAG, "Got null notification value in GCM data bundle");
            return;
        }

        String title = notification.getString("title");
        String body = notification.getString("body");

        if (body == null) {
            Log.w(TAG, "Got null notification body");
            return;
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);

        if (title != null) {
            notificationBuilder.setContentTitle(title);
        } else {
            notificationBuilder.setContentTitle("Notification");
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(this.random.nextInt(), notificationBuilder.build());
    }
}
