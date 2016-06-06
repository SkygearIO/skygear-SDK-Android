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
    public final String endpoint;
    /**
     * Skygear Api key.
     */
    public final String apiKey;

    private Configuration(String endpoint, String apiKey) {
        this.endpoint = endpoint;
        this.apiKey = apiKey;
    }

    /**
     * Default configuration
     *
     * @return a default configuration
     */
    static Configuration defaultConfiguration() {
        return new Configuration(
                DEFAULT_BASE_URL,
                DEFAULT_API_KEY
        );
    }

    /**
     * Configuration Builder.
     */
    public static final class Builder {
        private String endpoint;
        private String apiKey;

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

            return new Configuration(this.endpoint, this.apiKey);
        }
    }
}
