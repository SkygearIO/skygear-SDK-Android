package io.skygear.skygear;

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
    final String endpoint;

    /**
     * Skygear Api key.
     */
    final String apiKey;

    /**
     * Boolean indicating whether Pubsub Handler Execution is in Background.
     */
    final boolean pubsubHandlerExecutionInBackground;

    private Configuration(String endpoint, String apiKey, boolean pubsubHandlerExecutionInBackground) {
        this.endpoint = endpoint;
        this.apiKey = apiKey;
        this.pubsubHandlerExecutionInBackground = pubsubHandlerExecutionInBackground;
    }

    /**
     * Gets Skygear Endpoint.
     *
     * @return the endpoint
     */
    public String getEndpoint() {
        return new String(endpoint);
    }

    /**
     * Gets Skygear Api key.
     *
     * @return the api key
     */
    public String getApiKey() {
        return new String(apiKey);
    }

    /**
     * Is pubsub handler execution in background boolean.
     *
     * @return the boolean
     */
    public boolean isPubsubHandlerExecutionInBackground() {
        return pubsubHandlerExecutionInBackground;
    }

    /**
     * Default configuration
     *
     * @return a default configuration
     */
    static Configuration defaultConfiguration() {
        return new Configuration(
                DEFAULT_BASE_URL,
                DEFAULT_API_KEY,
                false
        );
    }

    /**
     * Configuration Builder.
     */
    public static final class Builder {
        private String endpoint;
        private String apiKey;
        private boolean pubsubHandlerExecutionInBackground;

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

        public Builder pubsubHandlerExecutionInBackground(boolean isInBackground) {
            this.pubsubHandlerExecutionInBackground = isInBackground;
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

            return new Configuration(
                    this.endpoint,
                    this.apiKey,
                    this.pubsubHandlerExecutionInBackground
            );
        }
    }
}
