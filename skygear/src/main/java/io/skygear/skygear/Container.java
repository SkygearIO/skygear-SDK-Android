package io.skygear.skygear;

import android.content.Context;
import android.util.Log;

import java.security.InvalidParameterException;

/**
 * Container for Skygear.
 */
public final class Container implements AuthResolver {
    private static final String TAG = "Skygear SDK";
    private static Container sharedInstance;

    private final PersistentStore persistentStore;
    private final Context context;
    private final Pubsub pubsub;
    private Configuration config;
    private RequestManager requestManager;
    private Database publicDatabase;
    private Database privateDatabase;

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
        this.pubsub = new Pubsub(this);
        this.persistentStore = new PersistentStore(context);
        this.publicDatabase = Database.publicDatabase(this);
        this.privateDatabase = Database.privateDatabase(this);

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
     * Gets pubsub.
     *
     * @return the pubsub
     */
    public Pubsub getPubsub() {
        return pubsub;
    }

    /**
     * Gets the public database.
     *
     * @return the public database
     */
    public Database getPublicDatabase() {
        return publicDatabase;
    }

    /**
     * Gets the private database.
     *
     * @return the private database
     * @throws AuthenticationException the authentication exception
     */
    public Database getPrivateDatabase() throws AuthenticationException {
        if (this.getCurrentUser() == null) {
            throw new AuthenticationException("Private database is only available for logged-in user");
        }

        return privateDatabase;
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
     * Gets current user.
     *
     * @return the current user
     */
    public User getCurrentUser() {
        return this.persistentStore.currentUser;
    }

    /**
     * Sign up with username.
     *
     * @param username the username
     * @param password the password
     * @param handler  the response handler
     */
    public void signupWithUsername(String username, String password, AuthResponseHandler handler) {
        Request req = new SignupRequest(username, null, password);
        req.responseHandler = new AuthResponseHandlerWrapper(this, handler);

        this.requestManager.sendRequest(req);
    }

    /**
     * Sign up with email.
     *
     * @param email    the email
     * @param password the password
     * @param handler  the response handler
     */
    public void signupWithEmail(String email, String password, AuthResponseHandler handler) {
        Request req = new SignupRequest(null, email, password);
        req.responseHandler = new AuthResponseHandlerWrapper(this, handler);

        this.requestManager.sendRequest(req);
    }

    /**
     * Sign up anonymously.
     *
     * @param handler the handler
     */
    public void signupAnonymously(AuthResponseHandler handler) {
        Request req = new SignupRequest();
        req.responseHandler = new AuthResponseHandlerWrapper(this, handler);

        this.requestManager.sendRequest(req);
    }

    /**
     * Login with username.
     *
     * @param username the username
     * @param password the password
     * @param handler  the response handler
     */
    public void loginWithUsername(String username, String password, AuthResponseHandler handler) {
        Request req = new LoginRequest(username, null, password);
        req.responseHandler = new AuthResponseHandlerWrapper(this, handler);

        this.requestManager.sendRequest(req);
    }

    /**
     * Login with email.
     *
     * @param email    the email
     * @param password the password
     * @param handler  the response handler
     */
    public void loginWithEmail(String email, String password, AuthResponseHandler handler) {
        Request req = new LoginRequest(null, email, password);
        req.responseHandler = new AuthResponseHandlerWrapper(this, handler);

        this.requestManager.sendRequest(req);
    }

    /**
     * Logout.
     *
     * @param handler the response handler
     */
    public void logout(LogoutResponseHandler handler) {
        final Request logoutRequest = new LogoutRequest();
        logoutRequest.responseHandler = new LogoutResponseHandlerWrapper(this, handler);

        String deviceId = this.persistentStore.deviceId;
        if (this.getCurrentUser() != null && deviceId != null) {
            // Try to unregister the device token before login out
            this.unregisterDeviceToken(new UnregisterDeviceResponseHandler() {
                @Override
                public void onUnregisterSuccess(String deviceId) {
                    Container.this.requestManager.sendRequest(logoutRequest);
                }

                @Override
                public void onUnregisterError(Error error) {
                    Log.w(TAG, "Fail to unregister device", error);
                    Container.this.requestManager.sendRequest(logoutRequest);
                }
            });
        } else {
            this.requestManager.sendRequest(logoutRequest);
        }
    }

    /**
     * Asks "Who Am I?"
     *
     * This API gets current user from server using current access token.
     *
     * @param handler the handler
     */
    public void whoami(AuthResponseHandler handler) {
        Request req = new GetCurrentUserRequest();
        req.responseHandler = new AuthResponseHandlerWrapper(this, handler);

        this.requestManager.sendRequest(req);
    }

    /**
     * Register device token.
     *
     * @param token the token
     */
    public void registerDeviceToken(String token) {
        this.persistentStore.deviceToken = token;
        this.persistentStore.save();

        if (this.getCurrentUser() != null) {
            RegisterDeviceRequest request = new RegisterDeviceRequest(
                    this.persistentStore.deviceId,
                    this.persistentStore.deviceToken
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
                            error.getMessage()
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
                        error.getMessage()
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
        if (this.getCurrentUser() != null && deviceId != null) {
            UnregisterDeviceRequest request = new UnregisterDeviceRequest(deviceId);
            request.responseHandler = handler;

            this.requestManager.sendRequest(request);
        }
    }

    @Override
    public void resolveAuthUser(User user) {
        this.persistentStore.currentUser = user;
        this.persistentStore.save();

        this.requestManager.accessToken = user != null ? user.accessToken : null;
        this.registerDeviceToken(this.persistentStore.deviceToken);
    }

    /**
     * Gets default access control.
     *
     * @return the access control
     */
    public AccessControl getDefaultAccessControl() {
        return AccessControl.defaultAccessControl();
    }

    /**
     * Sets default access control.
     *
     * @param accessControl the access control
     */
    public void setDefaultAccessControl(AccessControl accessControl) {
        this.persistentStore.defaultAccessControl = accessControl;
        this.persistentStore.save();

        AccessControl.defaultAccessControl = accessControl;
    }

    /**
     * Sets admin roles.
     *
     * @param role    the roles array
     * @param handler the handler
     */
    public void setAdminRole(Role[] role, SetRoleResponseHandler handler) {
        SetAdminRoleRequest request = new SetAdminRoleRequest(role);
        request.responseHandler = handler;

        this.requestManager.sendRequest(request);
    }

    /**
     * Sets an admin role.
     *
     * @param role    the role
     * @param handler the handler
     */
    public void setAdminRole(Role role, SetRoleResponseHandler handler) {
        this.setAdminRole(new Role[] { role }, handler);
    }

    /**
     * Sets default roles.
     *
     * @param role    the role array
     * @param handler the handler
     */
    public void setDefaultRole(Role[] role, SetRoleResponseHandler handler) {
        SetDefaultRoleRequest request = new SetDefaultRoleRequest(role);
        request.responseHandler = handler;

        this.requestManager.sendRequest(request);
    }

    /**
     * Sets a default role.
     *
     * @param role    the role
     * @param handler the handler
     */
    public void setDefaultRole(Role role, SetRoleResponseHandler handler) {
        this.setDefaultRole(new Role[] { role }, handler);
    }

    /**
     * Gets user by email.
     *
     * @param email   the email
     * @param handler the response handler
     */
    public void getUserByEmail(String email, UserQueryResponseHandler handler) {
        this.getUserByEmails(new String[] { email }, handler);
    }

    /**
     * Gets user by emails.
     *
     * @param emails  the emails
     * @param handler the response handler
     */
    public void getUserByEmails(String[] emails, UserQueryResponseHandler handler) {
        UserQueryRequest request = new UserQueryRequest(emails);
        request.responseHandler = handler;

        this.requestManager.sendRequest(request);
    }

    /**
     * Save user.
     *
     * @param user    the user
     * @param handler the response handler
     */
    public void saveUser(User user, UserSaveResponseHandler handler) {
        UserSaveRequest request = new UserSaveRequest(user);
        request.responseHandler = handler;

        this.requestManager.sendRequest(request);
    }

    /**
     * Upload asset.
     *
     * @param asset           the asset
     * @param responseHandler the response handler
     */
    public void uploadAsset(
            final Asset asset,
            final AssetPostRequest.ResponseHandler responseHandler
    ) {
        final RequestManager requestManager = this.requestManager;

        AssetPreparePostRequest preparePostRequest = new AssetPreparePostRequest(asset);
        preparePostRequest.responseHandler = new AssetPreparePostResponseHandler(asset) {
            @Override
            public void onPreparePostSuccess(AssetPostRequest postRequest) {
                postRequest.responseHandler = responseHandler;
                requestManager.sendAssetPostRequest(postRequest);
            }

            @Override
            public void onPreparePostFail(Error error) {
                if (responseHandler != null) {
                    responseHandler.onPostFail(asset, error);
                }
            }
        };

        requestManager.sendRequest(preparePostRequest);
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
