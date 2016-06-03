package io.skygear.skygear;

import java.security.InvalidParameterException;

/**
 * Container for Skygear.
 */
public final class Container {
    private static Container sharedInstance;
    private Configuration config;

    /**
     * Instantiates a new Container.
     *
     * @param config configuration of the container
     */
    public Container(Configuration config) {
        this.config = config;
    }

    /**
     * Gets the Default container shared within the application.
     *
     * This container is configured with default configuration. Better to configure it according to your usage.
     *
     * @return a default container
     */
    public static Container defaultContainer() {
        if (sharedInstance == null) {
            sharedInstance = new Container(Configuration.defaultConfiguration());
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
}
