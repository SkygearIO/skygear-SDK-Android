package io.skygear.skygear;

import android.content.Context;

import java.security.InvalidParameterException;

/**
 * Container for Skygear.
 */
public final class Container implements AuthResolver {
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
        Request req = new LogoutRequest();
        req.responseHandler = new LogoutResponseHandlerWrapper(this, handler);

        this.requestManager.sendRequest(req);
    }

    @Override
    public void resolveAuthUser(User user) {
        this.persistentStore.currentUser = user;
        this.persistentStore.save();

        this.requestManager.accessToken = user != null ? user.accessToken : null;
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
     * Send a request.
     *
     * @param request the request
     */
    public void sendRequest(Request request) {
        this.requestManager.sendRequest(request);
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
     * Gets context.
     *
     * @return the application context
     */
    public Context getContext() {
        return this.context;
    }

    /**
     * Gets current user.
     *
     * @return the current user
     */
    public User getCurrentUser() {
        return this.persistentStore.currentUser;
    }
}
