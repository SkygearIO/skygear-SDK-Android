package io.skygear.skygear;

import android.util.Log;

import java.lang.ref.WeakReference;
import java.security.InvalidParameterException;

/**
 * The Skygear PushContainer.
 */
public class PushContainer {
    private static final String TAG = "Skygear SDK";

    private WeakReference<Container> containerRef;

    public PushContainer(Container container) {
        this.containerRef = new WeakReference<>(container);
    }

    /**
     * Gets container.
     *
     * @return the container
     */
    public Container getContainer() {
        Container container = this.containerRef.get();
        if (container == null) {
            throw new InvalidParameterException("Missing container for database");
        }

        return container;
    }

    /**
     * Gets GCM Sender ID.
     *
     * @return the sender id
     */
    public String getGcmSenderId() {
        return this.getContainer().getConfig().getGcmSenderId();
    }

    /**
     * Register device token.
     *
     * @param token the token
     */
    public void registerDeviceToken(String token) {
        Container container = this.getContainer();
        container.persistentStore.deviceToken = token;
        container.persistentStore.save();

        if (container.auth.getCurrentUser() != null) {
            RegisterDeviceRequest request = new RegisterDeviceRequest(
                    container.persistentStore.deviceId,
                    container.persistentStore.deviceToken,
                    container.getContext().getPackageName()
            );

            request.responseHandler = new RegisterDeviceResponseHandler() {
                @Override
                public void onRegisterSuccess(String deviceId) {
                    Container container = PushContainer.this.getContainer();
                    container.persistentStore.deviceId = deviceId;
                    container.persistentStore.save();

                    Log.i(PushContainer.TAG, "Successfully register device with ID = " + deviceId);
                }

                @Override
                public void onRegisterError(Error error) {
                    Log.w(PushContainer.TAG, String.format(
                            "Fail to register device token: %s",
                            error.getDetailMessage()
                    ));
                }
            };

            container.requestManager.sendRequest(request);
        }
    }

    /**
     * Unregister device token.
     */
    public void unregisterDeviceToken() {
        this.getContainer().push.unregisterDeviceToken(new UnregisterDeviceResponseHandler() {
            @Override
            public void onUnregisterSuccess(String deviceId) {
                Log.i(PushContainer.TAG, "Successfully register device with ID = " + deviceId);
            }

            @Override
            public void onUnregisterError(Error error) {
                Log.w(PushContainer.TAG, String.format(
                        "Fail to unregister device token: %s",
                        error.getDetailMessage()
                ));
            }
        });
    }

    /**
     * Unregister device token.
     *
     * @param handler the response handler
     */
    public void unregisterDeviceToken(UnregisterDeviceResponseHandler handler) {
        Container container = this.getContainer();
        String deviceId = container.persistentStore.deviceId;
        if (container.auth.getCurrentUser() != null && deviceId != null) {
            UnregisterDeviceRequest request = new UnregisterDeviceRequest(deviceId);
            request.responseHandler = handler;

            container.requestManager.sendRequest(request);
        }
    }
}
