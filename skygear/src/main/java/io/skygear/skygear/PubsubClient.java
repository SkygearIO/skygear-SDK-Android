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
import android.os.HandlerThread;
import android.os.Handler;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * The Skygear PubsubClient.
 */
class PubsubClient implements WebSocketClientImpl.EventHandler {
    /**
     * The constant to indicate infinite retry limit.
     */
    public static final long RETRY_LIMIT_INFINITE = -1;
    /**
     * The constant to tell minimum retry .
     */
    public static final long MIN_RETRY_WAIT = 100;

    private static final String TAG = "Skygear SDK";
    private static final long DEFAULT_RETRY_LIMIT = RETRY_LIMIT_INFINITE;
    private static final long DEFAULT_RETRY_WAIT = 3000;

    /**
     * The Handlers Map
     * <p>
     * Mapping Channel Name to PubsubClient Handlers
     * </p>
     */
    final Map<String, Set<PubsubHandler>> handlers;

    private URI uri;
    private String apiKey;
    private long retryCount;
    private WeakReference<Container> containerRef;
    private WeakReference<PubsubListener> listenerRef;
    private boolean handlerExecutionInBackground;
    private HandlerThread backgroundThread;

    /**
     * The WebSocket Client.
     */
    WebSocketClient webSocket;

    /**
     * The Pending Messages.
     */
    Queue<Message> pendingMessages;

    /**
     * Instantiates a new Skygear PubsubClient.
     * <p>
     * Please be reminded that the skygear container passed in would be weakly referenced.
     * </p>
     *
     * @param container the skygear container
     */
    public PubsubClient(Container container) {
        super();

        this.containerRef = new WeakReference<>(container);
        this.listenerRef = new WeakReference<>(null);
        this.handlers = new HashMap<>();
        this.pendingMessages = new LinkedList<>();
        this.retryCount = 0;
        this.handlerExecutionInBackground = false;

        Configuration config = container.getConfig();
        if (config != null) {
            this.configure(config);
        }
    }

    /**
     * Updates the Configuration.
     * <p>
     * Please be reminded that the connection will be re-initiate after the config update.
     * </p>
     *
     * @param config the skygear config
     */
    void configure(Configuration config) {
        this.apiKey = config.apiKey;

        URI endpoint;
        try {
            endpoint = new URI(config.endpoint);
        } catch (URISyntaxException e) {
            throw new InvalidParameterException("Invalid endpoint format");
        }

        String host = endpoint.getHost();

        int port = endpoint.getPort();
        if (port != -1) {
            host += ":" + port;
        }

        String scheme = endpoint.getScheme();
        if (scheme.equalsIgnoreCase("http")) {
            scheme = "ws";
        } else if (scheme.equalsIgnoreCase("https")) {
            scheme = "wss";
        } else {
            throw new InvalidParameterException("Unexpected endpoint scheme: " + scheme);
        }

        this.uri = URI.create(String.format(
                "%s://%s/pubsub?api_key=%s",
                scheme,
                host,
                this.apiKey
        ));

        this.handlerExecutionInBackground = config.isPubsubHandlerExecutionInBackground();

        this.connect();
    }

    /**
     * Gets container.
     *
     * @return the container
     */
    Container getContainer() {
        Container container = this.containerRef.get();
        if (container == null) {
            throw new InvalidParameterException("Missing container for pubsubClient");
        }

        return container;
    }

    /**
     * Gets pubsubClient endpoint.
     *
     * @return the pubsubClient endpoint
     */
    String getPubsubEndpoint() {
        return this.uri.toString();
    }

    /**
     * Gets retry limit.
     *
     * @return the retry limit
     */
    long getRetryLimit() {
        return DEFAULT_RETRY_LIMIT;
    }

    /**
     * Gets retry wait time.
     *
     * @return the retry wait time
     */
    long getRetryWaitTime() {
        return DEFAULT_RETRY_WAIT;
    }

    private long getBoundedRetryWaitTime() {
        long retryWaitTime = this.getRetryWaitTime();
        if (retryWaitTime < MIN_RETRY_WAIT) {
            return MIN_RETRY_WAIT;
        }

        return retryWaitTime;
    }

    /**
     * Checks whether it is connected.
     *
     * @return the boolean indicating whether it is connected
     */
    boolean isConnected() {
        return this.webSocket != null && this.webSocket.isOpen();
    }

    /**
     * Checks whether it is connecting.
     *
     * @return the boolean indicating whether it is connecting
     */
    boolean isConnecting() {
        return this.webSocket != null && this.webSocket.isConnecting();
    }

    /**
     * Connect.
     */
    void connect() {
        this.retryCount = 0;
        this.webSocket = null;
        this.reconnect();
    }

    private void reconnect() {
        long retryLimit = this.getRetryLimit();
        if (retryLimit != RETRY_LIMIT_INFINITE && this.retryCount > retryLimit) {
            Log.i(TAG, String.format("PubsubClient reconnection count > %d. Give up.", retryLimit));
            return;
        }

        if (this.isConnecting()) {
            long retryWaitTime = this.getBoundedRetryWaitTime();

            Log.i(TAG, String.format("PubsubClient connecting, retry in %dms", retryWaitTime));
            this.delayReconnect(retryWaitTime);
            return;
        }

        if (!this.isConnected()) {
            Log.i(TAG, String.format(
                    "[RetryCount=%d] PubsubClient Connecting to: %s",
                    this.retryCount,
                    this.getPubsubEndpoint()
            ));
            this.retryCount++;

            /*
             *  Note: org.java_websocket.client.WebSocketClient does not allow being reused.
             *        So, A new instance should be generated for each reconnection.
             */
            if (this.webSocket != null) {
                this.webSocket.cleanup();
            }

            this.webSocket = new WebSocketClientImpl(this.uri, this);
            this.webSocket.connect();
        }
    }

    private void delayReconnect(long delay) {
        Log.i(TAG, String.format("PubsubClient reconnect in %dms", delay));

        Context context = this.getContainer().getContext();
        Handler handler = new android.os.Handler(context.getMainLooper());

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                PubsubClient.this.reconnect();
            }
        }, delay);
    }

    private void sendWebSocketSubscribe(String channel) {
        try {
            if (!this.isConnected()) {
                throw new WebSocketClient.NotYetConnectedException("WebSocket not yet connected");
            }

            JSONObject request = new JSONObject();
            request.put("action", "sub");
            request.put("channel", channel);

            this.webSocket.sendMessage(request.toString());
        } catch (JSONException e) {
            throw new InvalidParameterException("Invalid JSON format");
        } catch (WebSocketClient.NotYetConnectedException e) {
            Log.i(TAG, "WebSocket not yet connected, skip sending subscribe.");
        }
    }

    private void sendWebSocketUnsubscribe(String channel) {
        try {
            if (!this.isConnected()) {
                throw new WebSocketClient.NotYetConnectedException("WebSocket not yet connected");
            }

            JSONObject request = new JSONObject();
            request.put("action", "unsub");
            request.put("channel", channel);

            this.webSocket.sendMessage(request.toString());
        } catch (JSONException e) {
            throw new InvalidParameterException("Invalid JSON format");
        } catch (WebSocketClient.NotYetConnectedException e) {
            Log.i(TAG, "WebSocket not yet connected, skip sending unsubscribe.");
        }
    }

    /**
     * Check whether Handler Execution is in Background Thread. (Default: false)
     *
     * @return the boolean
     */
    public boolean isHandlerExecutionInBackground() {
        return handlerExecutionInBackground;
    }

    /**
     * Subscribes to a channel.
     *
     * @param channel the channel name
     * @param handler the handler
     * @return the handler being registered
     */
    PubsubHandler subscribe(String channel, PubsubHandler handler) {
        String trimmed = channel.trim();
        if (trimmed.length() == 0) {
            throw new InvalidParameterException("Cannot subscribe to empty channel name");
        }

        if (handler == null) {
            throw new InvalidParameterException("Missing subscription handler");
        }

        Set<PubsubHandler> channelHandlers = this.handlers.get(trimmed);
        boolean isNewSubscription = false;

        if (channelHandlers == null) {
            channelHandlers = new HashSet<>();
            isNewSubscription = true;
        }

        channelHandlers.add(handler);
        this.handlers.put(trimmed, channelHandlers);

        if (isNewSubscription) {
            this.sendWebSocketSubscribe(trimmed);
        }

        return handler;
    }

    /**
     * Unsubscribes all handler from a channel.
     *
     * @param channel the channel name
     * @return all the handlers being removed
     */
    PubsubHandler[] unsubscribeAll(String channel) {
        String trimmed = channel.trim();
        if (trimmed.length() == 0) {
            throw new InvalidParameterException("Cannot unsubscribe to channel with empty name");
        }

        this.sendWebSocketUnsubscribe(trimmed);

        Set<PubsubHandler> channelHandlers = this.handlers.get(trimmed);
        if (channelHandlers != null) {
            this.handlers.remove(trimmed);

            PubsubHandler[] handlerArr = new PubsubHandler[channelHandlers.size()];
            channelHandlers.toArray(handlerArr);

            return handlerArr;
        }

        return new PubsubHandler[0];
    }

    /**
     * Unsubscribes a handler from a channel.
     *
     * @param channel the channel name
     * @param handler the handler
     * @return the handler being removed
     */
    PubsubHandler unsubscribe(String channel, PubsubHandler handler) {
        String trimmed = channel.trim();
        if (trimmed.length() == 0) {
            throw new InvalidParameterException("Cannot unsubscribe to channel with empty name");
        }

        if (handler == null) {
            throw new InvalidParameterException("Missing subscription handler");
        }

        Set<PubsubHandler> channelHandlers = this.handlers.get(trimmed);
        if (channelHandlers == null || channelHandlers.size() == 0) {
            return null;
        }

        channelHandlers.remove(handler);

        if (channelHandlers.size() > 0) {
            this.handlers.put(channel, channelHandlers);
        } else {
            this.handlers.remove(trimmed);
            this.sendWebSocketUnsubscribe(trimmed);
        }

        return handler;
    }

    /**
     * Publishes data to a channel
     *
     * @param channel the channel name
     * @param data    the data
     */
    void publish(String channel, JSONObject data) {
        String trimmed = channel.trim();
        if (trimmed.length() == 0) {
            throw new InvalidParameterException("Cannot publish event to channel with empty name");
        }

        if (data == null) {
            throw new InvalidParameterException("Missing data to publish");
        }

        try {
            if (!this.isConnected()) {
                throw new WebSocketClient.NotYetConnectedException("WebSocket not yet connected");
            }

            JSONObject request = new JSONObject();
            request.put("action", "pub");
            request.put("channel", channel);
            request.put("data", data);

            this.webSocket.sendMessage(request.toString());
        } catch (JSONException e) {
            throw new InvalidParameterException("Invalid JSON format");
        } catch (WebSocketClient.NotYetConnectedException e) {
            Log.i(TAG, "WebSocket not yet connected, message has been queued up.");
            this.pendingMessages.offer(new Message(channel, data));
        }
    }

    @Override
    public void onOpen(int statusCode, String statusMessage) {
        Log.i(TAG, String.format("PubsubClient connection opened: %d %s", statusCode, statusMessage));
        this.retryCount = 0;

        Set<String> allChannels = this.handlers.keySet();
        for (String perChannel : allChannels) {
            this.sendWebSocketSubscribe(perChannel);
        }

        Queue<Message> pendingMessages = this.pendingMessages;
        this.pendingMessages = new LinkedList<>();

        final PubsubListener listener = listenerRef.get();
        if (listener != null) {
            Handler handler = getHandler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onOpen();
                }
            });
        }

        while (pendingMessages.peek() != null) {
            Message message = pendingMessages.poll();
            this.publish(message.channel, message.data);
        }
    }

    @Override
    public void onMessage(JSONObject eventData) {
        String channel;
        try {
            channel = eventData.getString("channel");
        } catch (JSONException e) {
            throw new InvalidParameterException("Missing channel name on event data");
        }

        JSONObject data = null;
        try {
            data = eventData.getJSONObject("data");
        } catch (JSONException e) {
            Log.w(TAG, "Invalid JSON Object", e);
        }

        Handler handler = getHandler();

        Set<PubsubHandler> channelHandlers = this.handlers.get(channel);
        if (channelHandlers != null) {
            for (final PubsubHandler perHandler : channelHandlers) {
                final JSONObject perHandlerData = data;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        perHandler.handle(perHandlerData);
                    }
                });
            }
        }
    }

    @Override
    public void onError(final WebSocketClientImpl.Exception exception) {
        Log.i(TAG, "PubsubClient connection error: " + exception.getMessage());

        final PubsubListener listener = listenerRef.get();
        if (listener != null) {
            Handler handler = getHandler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onError(exception);
                }
            });
        }
    }

    @Override
    public void onClose(String reason) {

        Log.i(TAG, "PubsubClient connection close: " + reason);
        this.delayReconnect(this.getBoundedRetryWaitTime());

        final PubsubListener listener = listenerRef.get();

        if (listener != null) {
            Handler handler = getHandler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onClose();
                }
            });
        }
    }

    private Handler getHandler() {
        if (!this.handlerExecutionInBackground) {
            return new android.os.Handler(getContainer().getContext().getMainLooper());
        } else {
            if (this.backgroundThread == null || this.backgroundThread.getLooper() == null) {
                throw new IllegalStateException("Background thread is not yet created");
            }

            return new android.os.Handler(this.backgroundThread.getLooper());
        }
    }

    void setListener(PubsubListener listener) {
        listenerRef = new WeakReference<>(listener);
    }

    /**
     * The PubsubClient Message.
     */
    static class Message {
        /**
         * The Message Channel.
         */
        final String channel;
        /**
         * The Message Data.
         */
        final JSONObject data;

        /**
         * Instantiates a new PubsubClient Message.
         *
         * @param channel the pubsubClient channel
         * @param data    the pubsubClient data
         */
        Message(String channel, JSONObject data) {
            super();
            this.channel = channel;
            this.data = data;
        }
    }
}
