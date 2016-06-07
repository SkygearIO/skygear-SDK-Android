package io.skygear.skygear;

import android.app.Application;

/**
 * An extended application class for Skygear
 */
public abstract class SkygearApplication extends Application {
    /**
     * Gets Skygear endpoint.
     *
     * @return the Skygear endpoint
     */
    abstract public String getSkygearEndpoint();

    /**
     * Gets Skygear api key.
     *
     * @return the Skygear API key
     */
    abstract public String getApiKey();

    @Override
    public void onCreate() {
        super.onCreate();

        Configuration config = new Configuration.Builder()
                .endPoint(this.getSkygearEndpoint())
                .apiKey(this.getApiKey())
                .build();

        Container.defaultContainer(this).configure(config);
    }
}
