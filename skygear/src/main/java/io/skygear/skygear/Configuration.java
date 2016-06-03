package io.skygear.skygear;

import android.content.Context;

import java.security.InvalidParameterException;

/**
 * Configuration of Skygear.
 */
public final class Configuration {
    private static final String DEFAULT_BASE_URL = "http://skygear.dev/";
    private static final String DEFAULT_API_KEY = "changeme";

    public final String endpoint;
    public final String apiKey;
    public final Context context;

    private Configuration(String endpoint, String apiKey, Context context) {
        this.endpoint = endpoint;
        this.apiKey = apiKey;
        this.context = context;
    }

    protected static Configuration defaultConfiguration() {
        return new Configuration(
                DEFAULT_BASE_URL,
                DEFAULT_API_KEY,
                null
        );
    }

    public static final class Builder {
        private String endpoint;
        private String apiKey;
        private Context context;

        public Builder endPoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder context(Context context) {
            this.context = context;
            return this;
        }

        public Configuration build() {
            if (this.endpoint == null) {
                throw new InvalidParameterException("Missing Skygear Endpoint");
            }

            if (this.apiKey == null) {
                throw new InvalidParameterException("Missing API Key");
            }

            return new Configuration(this.endpoint, this.apiKey, this.context);
        }
    }
}
