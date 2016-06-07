package io.skygear.skygear;

import android.content.Context;

import java.security.InvalidParameterException;

/**
 * Container for Skygear.
 */
public final class Container {
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
