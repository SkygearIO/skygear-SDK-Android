package io.skygear.skygear;

import android.content.Context;

import java.security.InvalidParameterException;

/**
 * Container for Skygear.
 */
public final class Container implements AuthResolver {
    private static Container sharedInstance;
    private Context context;
    private Configuration config;
    private RequestManager requestManager;

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

        if (handler != null) {
            handler.authResolver = this;
            req.responseHandler = handler;
        } else {
            req.responseHandler = new AuthResolveHandler(this);
        }

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

        if (handler != null) {
            handler.authResolver = this;
            req.responseHandler = handler;
        } else {
            req.responseHandler = new AuthResolveHandler(this);
        }

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

        if (handler != null) {
            handler.authResolver = this;
            req.responseHandler = handler;
        } else {
            req.responseHandler = new AuthResolveHandler(this);
        }

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

        if (handler != null) {
            handler.authResolver = this;
            req.responseHandler = handler;
        } else {
            req.responseHandler = new AuthResolveHandler(this);
        }

        this.requestManager.sendRequest(req);
    }

    /**
     * Logout.
     *
     * @param handler the response handler
     */
    public void logout(LogoutResponseHandler handler) {
        Request req = new LogoutRequest();

        if (handler != null) {
            handler.authResolver = this;
            req.responseHandler = handler;
        } else {
            req.responseHandler = new AuthResolveHandler(this);
        }

        this.requestManager.sendRequest(req);
    }

    @Override
    public void resolveAuthToken(String token) {
        this.requestManager.accessToken = token;
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
}
