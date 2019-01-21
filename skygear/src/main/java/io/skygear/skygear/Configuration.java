/*
 * Copyright 2017 Oursky Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.skygear.skygear;

import java.security.InvalidParameterException;

/**
 * Configuration of Skygear.
 */
public final class Configuration {
    private static final String TEST_BASE_URL = "http://skygear.dev/";
    private static final String TEST_API_KEY = "changeme";

    /**
     * Skygear Endpoint.
     */
    final String endpoint;

    /**
     * Skygear Api key.
     */
    final String apiKey;

    /**
     * Boolean indicating whether PubsubClient Handler Execution is in Background.
     */
    final boolean pubsubHandlerExecutionInBackground;

    /**
     * Boolean indicating whether PubsubClient is automatically connected upon
     * configuration.
     */
    final boolean pubsubConnectAutomatically;


    /**
     * Boolean indicating whether encrypt current user data saved in SharedPreferences.
     */
    final boolean encryptCurrentUserData;


    private Configuration(
            String endpoint,
            String apiKey,
            boolean pubsubHandlerExecutionInBackground,
            boolean pubsubConnectAutomatically,
            boolean encryptCurrentUserData
    ) {
        this.endpoint = endpoint;
        this.apiKey = apiKey;
        this.pubsubHandlerExecutionInBackground = pubsubHandlerExecutionInBackground;
        this.pubsubConnectAutomatically = pubsubConnectAutomatically;
        this.encryptCurrentUserData = encryptCurrentUserData;
    }

    /**
     * Gets Skygear Endpoint.
     *
     * @return the endpoint
     */
    public String getEndpoint() {
        return endpoint;
    }

    /**
     * Gets Skygear Api key.
     *
     * @return the api key
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * Is pubsubClient handler execution in background boolean.
     *
     * @return the boolean
     */
    public boolean isPubsubHandlerExecutionInBackground() {
        return pubsubHandlerExecutionInBackground;
    }

    /**
     * Is pubsubClient connect automatically boolean.
     *
     * @return the boolean
     */
    public boolean isPubsubConnectAutomatically() {
        return pubsubConnectAutomatically;
    }

    /**
     * Encrypt current user data boolean.
     *
     * @return the boolean
     */
    public boolean encryptCurrentUserData() {
        return encryptCurrentUserData;
    }

    /**
     * Creates an instance of default configuration.
     *
     * This method is deprecated. You should create configuration by
     * providing Endpoint and API key.
     *
     * @return a default configuration
     */
    @Deprecated static Configuration defaultConfiguration() {
        return new Builder()
                .endPoint(TEST_BASE_URL)
                .apiKey(TEST_API_KEY)
                .build();
    }

    /**
     * Creates an instance of test configuration.
     *
     * @return a test configuration
     */
    static Configuration testConfiguration() {
        return new Builder()
                .endPoint(TEST_BASE_URL)
                .apiKey(TEST_API_KEY)
                .pubsubConnectAutomatically(false)
                .build();
    }

    /**
     * Configuration Builder.
     */
    public static final class Builder {
        private String endpoint;
        private String apiKey;
        private boolean pubsubHandlerExecutionInBackground;
        private boolean pubsubConnectAutomatically;
        private boolean encryptCurrentUserData;

        /**
         * Creates an instance of Builder.
         */
        public Builder() {
            this.pubsubConnectAutomatically = true;
        }

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
         * Sets whether PubsubClient Handlers Execution Should be in Background.
         *
         * @param isInBackground the boolean indicating whether the execution is in background
         * @return the builder
         */
        public Builder pubsubHandlerExecutionInBackground(boolean isInBackground) {
            this.pubsubHandlerExecutionInBackground = isInBackground;
            return this;
        }

        /**
         * Sets whether PubsubClient connect automatically.
         *
         * @param automatic the boolean indicating whether connection is made
         * automatically.
         * @return the builder
         */
        public Builder pubsubConnectAutomatically(boolean automatic) {
            this.pubsubConnectAutomatically = automatic;
            return this;
        }

        /**
         * Sets whether encrypt current user data saved in SharedPreferences.
         *
         * @param enabled the boolean indicating whether encryption Enabled
         * automatically.
         * @return the builder
         */
        public Builder encryptCurrentUserData(boolean enabled) {
            this.encryptCurrentUserData = enabled;
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
                    this.pubsubHandlerExecutionInBackground,
                    this.pubsubConnectAutomatically,
                    this.encryptCurrentUserData
            );
        }
    }
}
