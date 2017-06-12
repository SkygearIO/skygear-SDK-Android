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

    /**
     * Gets GCM Sender ID.
     *
     * @return the sender id
     */
    public String getGcmSenderId() {
        return null;
    }

    /**
     * Gets whether the PubsubClient Handler Execution Should be in Background.
     *
     * @return the boolean indicating whether the execution is in background
     */
    public boolean isPubsubHandlerExecutionInBackground() {
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Configuration config = new Configuration.Builder()
                .endPoint(this.getSkygearEndpoint())
                .apiKey(this.getApiKey())
                .gcmSenderId(this.getGcmSenderId())
                .pubsubHandlerExecutionInBackground(this.isPubsubHandlerExecutionInBackground())
                .build();

        Container.defaultContainer(this).configure(config);
    }
}
