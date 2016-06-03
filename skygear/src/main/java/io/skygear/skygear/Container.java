package io.skygear.skygear;

import java.security.InvalidParameterException;

/**
 * Container for Skygear.
 */
public final class Container {
    private static Container sharedInstance;
    private Configuration config;

    public Container(Configuration config) {
        this.config = config;
    }

    public static Container defaultContainer() {
        if (sharedInstance == null) {
            sharedInstance = new Container(Configuration.defaultConfiguration());
        }

        return sharedInstance;
    }

    public void configure(Configuration config) {
        if (config == null) {
            throw new InvalidParameterException("Null configuration is not allowed");
        }

        this.config = config;
    }
}
