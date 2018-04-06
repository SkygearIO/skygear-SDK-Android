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
     * GCM Sender ID
     */
    final String gcmSenderId;

    /**
     * Boolean indicating whether PubsubClient Handler Execution is in Background.
     */
    final boolean pubsubHandlerExecutionInBackground;

    private Configuration(
            String endpoint,
            String apiKey,
            String gcmSenderId,
            boolean pubsubHandlerExecutionInBackground
    ) {
        this.endpoint = endpoint;
        this.apiKey = apiKey;
        this.gcmSenderId = gcmSenderId;
        this.pubsubHandlerExecutionInBackground = pubsubHandlerExecutionInBackground;
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
     * Gets GCM Sender ID.
     *
     * @return the sender id
     */
    public String getGcmSenderId() {
        return gcmSenderId;
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
                .build();
    }

    /**
     * Configuration Builder.
     */
    public static final class Builder {
        private String endpoint;
        private String apiKey;
        private String gcmSenderId;
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

        /**
         * Sets the GCM Sender ID.
         *
         * @param senderId the sender id
         * @return the builder
         */
        public Builder gcmSenderId(String senderId) {
            this.gcmSenderId = senderId;
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
                    this.gcmSenderId,
                    this.pubsubHandlerExecutionInBackground
            );
        }
    }
}
