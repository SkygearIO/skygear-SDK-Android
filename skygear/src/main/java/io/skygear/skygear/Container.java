package io.skygear.skygear;

import android.content.Context;
import android.util.Log;

import java.security.InvalidParameterException;

/**
 * Container for Skygear.
 */
public final class Container {
    private static final String TAG = "Skygear SDK";
    private static Container sharedInstance;

    final PersistentStore persistentStore;
    final Context context;
    final PubsubContainer pubsub;
    final RequestManager requestManager;
    final PublicDatabase publicDatabase;
    final Database privateDatabase;
    Configuration config;

    private final AuthContainer auth;

    /**
     * Instantiates a new Container.
     *
     * @param context application context
     * @param config  configuration of the container
     */
    public Container(Context context, Configuration config) {
        this.context = context.getApplicationContext();
        this.config = config;
        this.requestManager = new RequestManager(context, config);
        this.pubsub = new PubsubContainer(this);
        this.persistentStore = new PersistentStore(context);
        this.publicDatabase = Database.Factory.publicDatabase(this);
        this.privateDatabase = Database.Factory.privateDatabase(this);

        this.auth = new AuthContainer(this);

        if (this.persistentStore.currentUser != null) {
            this.requestManager.accessToken = this.persistentStore.currentUser.accessToken;
        }

        if (this.persistentStore.defaultAccessControl != null) {
            AccessControl.defaultAccessControl = this.persistentStore.defaultAccessControl;
        }
    }

    /**
     * Gets the Default container shared within the application.
     * This container is configured with default configuration. Better to configure it according to your usage.
     *
     * @param context application context
     * @return a default container
     */
    public static Container defaultContainer(Context context) {
        if (sharedInstance == null) {
            sharedInstance = new Container(context, Configuration.defaultConfiguration());
        }

        return sharedInstance;
    }

    /**
     * @return auth
     */
    public AuthContainer auth() {
        return auth;
    }

    /**
     * Gets the public database.
     *
     * @return the public database
     */
    public Database publicDatabase() {
        return publicDatabase;
    }

    /**
     * Gets the private database.
     *
     * @return the private database
     * @throws AuthenticationException the authentication exception
     */
    public Database privateDatabase() throws AuthenticationException {
        if (this.auth.getCurrentUser() == null) {
            throw new AuthenticationException("Private database is only available for logged-in user");
        }

        return privateDatabase;
    }

    /**
     * Gets pubsubContainer.
     *
     * @return the pusbubContainer
     */
    public PubsubContainer pubsub() {
        return pubsub;
    }

    /**
     * Updates configuration of the container
     *
     * @param config configuration of the container
     */
    public void configure(Configuration config) {
        if (config == null) {
            throw new InvalidParameterException("Null configuration is not allowed");
        }

        this.config = config;
        this.requestManager.configure(config);
        this.pubsub.configure(config);
    }

    /**
     * Gets context.
     *
     * @return the application context
     */
    public Context getContext() {
        return this.context;
    }

    /**
     * Gets config.
     *
     * @return the config
     */
    public Configuration getConfig() {
        return config;
    }

    /**
     * Gets GCM Sender ID.
     *
     * @return the sender id
     */
    public String getGcmSenderId() {
        return this.getConfig().getGcmSenderId();
    }

    /**
     * Sets request timeout (in milliseconds).
     *
     * @param timeout the timeout
     */
    public void setRequestTimeout(int timeout) {
        this.requestManager.requestTimeout = timeout;
    }

    /**
     * Gets request timeout (in milliseconds).
     *
     * @return the request timeout
     */
    public int getRequestTimeout() {
        return this.requestManager.requestTimeout;
    }

    /**
     * Send a request.
     *
     * @param request the request
     */
    public void sendRequest(Request request) {
        this.requestManager.sendRequest(request);
    }

    /**
     * Register device token.
     *
     * @param token the token
     */
    public void registerDeviceToken(String token) {
        this.persistentStore.deviceToken = token;
        this.persistentStore.save();

        if (this.auth.getCurrentUser() != null) {
            RegisterDeviceRequest request = new RegisterDeviceRequest(
                    this.persistentStore.deviceId,
                    this.persistentStore.deviceToken,
                    this.getContext().getPackageName()
            );

            request.responseHandler = new RegisterDeviceResponseHandler() {
                @Override
                public void onRegisterSuccess(String deviceId) {
                    Container.this.persistentStore.deviceId = deviceId;
                    Container.this.persistentStore.save();

                    Log.i(TAG, "Successfully register device with ID = " + deviceId);
                }

                @Override
                public void onRegisterError(Error error) {
                    Log.w(TAG, String.format(
                            "Fail to register device token: %s",
                            error.getDetailMessage()
                    ));
                }
            };

            this.requestManager.sendRequest(request);
        }
    }

    /**
     * Unregister device token.
     */
    public void unregisterDeviceToken() {
        this.unregisterDeviceToken(new UnregisterDeviceResponseHandler() {
            @Override
            public void onUnregisterSuccess(String deviceId) {
                Log.i(TAG, "Successfully register device with ID = " + deviceId);
            }

            @Override
            public void onUnregisterError(Error error) {
                Log.w(TAG, String.format(
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
        String deviceId = this.persistentStore.deviceId;
        if (this.auth.getCurrentUser() != null && deviceId != null) {
            UnregisterDeviceRequest request = new UnregisterDeviceRequest(deviceId);
            request.responseHandler = handler;

            this.requestManager.sendRequest(request);
        }
    }

    /**
     * Call lambda function.
     *
     * @param name    the function name
     * @param handler the response handler
     */
    public void callLambdaFunction(String name, LambdaResponseHandler handler) {
        this.callLambdaFunction(name, null, handler);
    }

    /**
     * Call lambda function.
     *
     * @param name    the function name
     * @param args    the arguments
     * @param handler the response handler
     */
    public void callLambdaFunction(String name, Object[] args, LambdaResponseHandler handler) {
        LambdaRequest request = new LambdaRequest(name, args);
        request.responseHandler = handler;

        this.requestManager.sendRequest(request);
    }
}
