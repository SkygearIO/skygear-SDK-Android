package io.skygear.skygear;

import android.content.Context;

import java.security.InvalidParameterException;

/**
 * Configuration of Skygear.
 */
public final class Configuration {
    private static final String DEFAULT_BASE_URL = "http://skygear.dev/";
    private static final String DEFAULT_API_KEY = "changeme";

    /**
     * Skygear Endpoint.
     */
    public final String endpoint;
    /**
     * Skygear Api key.
     */
    public final String apiKey;
    /**
     * Application Context.
     */
    public final Context context;

    private Configuration(String endpoint, String apiKey, Context context) {
        this.endpoint = endpoint;
        this.apiKey = apiKey;
        this.context = context.getApplicationContext();
    }

    /**
     * Default configuration
     *
     * @return a default configuration
     */
    protected static Configuration defaultConfiguration(Context context) {
        return new Configuration(
                DEFAULT_BASE_URL,
                DEFAULT_API_KEY,
                context
        );
    }

    /**
     * Configuration Builder.
     */
    public static final class Builder {
        private String endpoint;
        private String apiKey;
        private Context context;

        /**
         * Sets the Skygear endpoint.
         *
         * @param endpoint the endpoint
         * @return the builder
         */
        public Builder endPoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        /**
         * Sets the API key.
         *
         * @param apiKey the api key
         * @return the builder
         */
        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        /**
         * Sets the application context.
         *
         * @param context the application context
         * @return the builder
         */
        public Builder context(Context context) {
            this.context = context;
            return this;
        }

        /**
         * Build a configuration.
         *
         * @return the configuration
         */
        public Configuration build() {
            if (this.endpoint == null) {
                throw new InvalidParameterException("Missing Skygear Endpoint");
            }

            if (this.apiKey == null) {
                throw new InvalidParameterException("Missing API Key");
            }

            if (this.context == null) {
                throw new InvalidParameterException("Missing Application Context");
            }

            return new Configuration(this.endpoint, this.apiKey, this.context);
        }
    }
}
