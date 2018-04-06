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

import android.content.Context;

import java.security.InvalidParameterException;
import java.util.Map;

/**
 * Container for Skygear.
 */
public final class Container {
    private static final String TAG = "Skygear SDK";
    private static Container sharedInstance;

    final PersistentStore persistentStore;
    final Context context;
    final RequestManager requestManager;
    Configuration config;

    final AuthContainer auth;
    final PubsubContainer pubsub;
    final PushContainer push;
    final PublicDatabase publicDatabase;
    final Database privateDatabase;

    /**
     * Creates a Container without configuration.
     *
     * @param context application context
     */
    private Container(Context context) {
        this(context, (Configuration)null);
    }

    /**
     * Instantiates a new Container.
     *
     * @param context application context
     * @param config  configuration of the container
     */
    public Container(Context context, Configuration config) {
        this.context = context.getApplicationContext();
        this.config = config;
        this.requestManager = new RequestManager(this.context, this.config);
        this.persistentStore = new PersistentStore(this.context);

        this.auth = new AuthContainer(this);
        this.pubsub = new PubsubContainer(this);
        this.push = new PushContainer(this);
        this.publicDatabase = Database.Factory.publicDatabase(this);
        this.privateDatabase = Database.Factory.privateDatabase(this);
        this.requestManager.accessToken = this.persistentStore.accessToken;

        if (this.persistentStore.defaultAccessControl != null) {
            AccessControl.defaultAccessControl = this.persistentStore.defaultAccessControl;
        }
    }

    /**
     * Gets the Default container shared within the application.
     * This container is configured with default configuration. Better to configure it according to your usage.
     *
     * @param context application context
     * @return a default container
     */
    public static Container defaultContainer(Context context) {
        if (sharedInstance == null) {
            sharedInstance = new Container(context);
        }

        return sharedInstance;
    }

    /**
     * Gets auth container.
     *
     * @return the auth container
     */
    public AuthContainer getAuth() {
        return auth;
    }

    /**
     * Gets the public database.
     *
     * @return the public database
     */
    public PublicDatabase getPublicDatabase() {
        return publicDatabase;
    }

    /**
     * Gets the private database.
     *
     * @return the private database
     * @throws AuthenticationException the authentication exception
     */
    public Database getPrivateDatabase() throws AuthenticationException {
        if (this.auth.getCurrentUser() == null) {
            throw new AuthenticationException("Private database is only available for logged-in user");
        }

        return privateDatabase;
    }

    /**
     * Gets pubsub container.
     *
     * @return the pusbub container
     */
    public PubsubContainer getPubsub() {
        return pubsub;
    }

    /**
     * Gets push container.
     *
     * @return the push container
     */
    public PushContainer getPush() {
        return push;
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
        this.requestManager.configure(config);
        this.pubsub.configure(config);
    }

    /**
     * Gets context.
     *
     * @return the application context
     */
    public Context getContext() {
        return this.context;
    }

    /**
     * Gets config.
     *
     * @return the config
     */
    public Configuration getConfig() {
        return config;
    }

    /**
     * Sets request timeout (in milliseconds).
     *
     * @param timeout the timeout
     */
    public void setRequestTimeout(int timeout) {
        this.requestManager.requestTimeout = timeout;
    }

    /**
     * Gets request timeout (in milliseconds).
     *
     * @return the request timeout
     */
    public int getRequestTimeout() {
        return this.requestManager.requestTimeout;
    }

    /**
     * Send a request.
     *
     * @param request the request
     */
    public void sendRequest(Request request) {
        this.requestManager.sendRequest(request);
    }

    /**
     * Call lambda function.
     *
     * @param name    the function name
     * @param handler the response handler
     */
    public void callLambdaFunction(String name, LambdaResponseHandler handler) {
        this.callLambdaFunction(name, (Object[]) null, handler);
    }

    /**
     * Call lambda function.
     *
     * @param name    the function name
     * @param args    the arguments
     * @param handler the response handler
     */
    public void callLambdaFunction(String name, Object[] args, LambdaResponseHandler handler) {
        LambdaRequest request = new LambdaRequest(name, args);
        request.responseHandler = handler;

        this.requestManager.sendRequest(request);
    }

    /**
     * Call lambda function with arguments map.
     *
     * @param name    the function name
     * @param args    the arguments map
     * @param handler the response handler
     */
    public void callLambdaFunction(String name, Map<String, Object> args, LambdaResponseHandler handler) {
        LambdaRequest request = new LambdaRequest(name, args);
        request.responseHandler = handler;

        this.requestManager.sendRequest(request);
    }
}
