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

import org.json.JSONObject;

/**
 * The Skygear PubsubContainer.
 */
public class PubsubContainer {
    private static final String TAG = "Skygear SDK";

    private final PubsubClient pubsubClient;

    public PubsubContainer(Container container) {
        this.pubsubClient = new PubsubClient(container);
    }

    /**
     * Updates the Configuration.
     * <p>
     * Please be reminded that the connection will be re-initiate after the config update.
     * </p>
     *
     * @param config the skygear config
     */
    public void configure(Configuration config) {
        if (config != null) {
            pubsubClient.configure(config);
        }
    }

    /**
     * Gets pubsubClient endpoint.
     *
     * @return the pubsubClient endpoint
     */
    public String getPubsubEndpoint() {
        return pubsubClient.getPubsubEndpoint();
    }

    /**
     * Gets retry limit.
     *
     * @return the retry limit
     */
    public long getRetryLimit() {
        return pubsubClient.getRetryLimit();
    }

    /**
     * Gets retry wait time.
     *
     * @return the retry wait time
     */
    public long getRetryWaitTime() {
        return pubsubClient.getRetryWaitTime();
    }

    /**
     * Checks whether it is connected.
     *
     * @return the boolean indicating whether it is connected
     */
    public boolean isConnected() {
        return pubsubClient.isConnected();
    }

    /**
     * Checks whether it is connecting.
     *
     * @return the boolean indicating whether it is connecting
     */
    public boolean isConnecting() {
        return pubsubClient.isConnecting();
    }

    /**
     * Connect.
     */
    public void connect() {
        pubsubClient.connect();
    }

    /**
     * Check whether Handler Execution is in Background Thread. (Default: false)
     *
     * @return the boolean
     */
    public boolean isHandlerExecutionInBackground() {
        return pubsubClient.isHandlerExecutionInBackground();
    }

    /**
     * Subscribes to a channel.
     *
     * @param channel the channel name
     * @param handler the handler
     * @return the handler being registered
     */
    public PubsubHandler subscribe(String channel, PubsubHandler handler) {
        return pubsubClient.subscribe(channel, handler);
    }

    /**
     * Unsubscribes all handler from a channel.
     *
     * @param channel the channel name
     * @return all the handlers being removed
     */
    public PubsubHandler[] unsubscribeAll(String channel) {
        return pubsubClient.unsubscribeAll(channel);
    }

    /**
     * Unsubscribes a handler from a channel.
     *
     * @param channel the channel name
     * @param handler the handler
     * @return the handler being removed
     */
    public PubsubHandler unsubscribe(String channel, PubsubHandler handler) {
        return pubsubClient.unsubscribe(channel, handler);
    }

    /**
     * Publishes data to a channel
     *
     * @param channel the channel name
     * @param data    the data
     */
    public void publish(String channel, JSONObject data) {
        pubsubClient.publish(channel, data);
    }

    /**
     * Set Pubsub Listener
     *
     * @param listener the listener
     */
    public void setListener(PubsubListener listener) { pubsubClient.setListener(listener); }
}
