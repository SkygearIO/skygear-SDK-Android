package io.skygear.skygear;

import android.content.Context;
import android.os.HandlerThread;
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
 * The Skygear Pubsub.
 */
public class Pubsub implements WebSocketClientImpl.EventHandler {
    /**
     * The constant to indicate infinite retry limit.
     */
    public static final long RETRY_LIMIT_INFINITE = -1;
    /**
     * The constant to tell minimum retry .
     */
    public static final long MIN_RETRY_WAIT = 100;

    private static final String TAG = Pubsub.class.getSimpleName();
    private static final long DEFAULT_RETRY_LIMIT = RETRY_LIMIT_INFINITE;
    private static final long DEFAULT_RETRY_WAIT = 3000;

    /**
     * The Handlers Map
     * <p>
     * Mapping Channel Name to Pubsub Handlers
     * </p>
     */
    final Map<String, Set<Handler>> handlers;

    private URI uri;
    private String apiKey;
    private long retryCount;
    private WeakReference<Container> containerRef;
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
     * Instantiates a new Skygear Pubsub.
     * <p>
     * Please be reminded that the skygear container passed in would be weakly referenced.
     * </p>
     *
     * @param container the skygear container
     */
    public Pubsub(Container container) {
        super();

        this.containerRef = new WeakReference<>(container);
        this.handlers = new HashMap<>();
        this.pendingMessages = new LinkedList<>();
        this.retryCount = 0;
        this.handlerExecutionInBackground = false;

        this.configure(container.getConfig());
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
    public Container getContainer() {
        Container container = this.containerRef.get();
        if (container == null) {
            throw new InvalidParameterException("Missing container for pubsub");
        }

        return container;
    }

    /**
     * Gets pubsub endpoint.
     *
     * @return the pubsub endpoint
     */
    public String getPubsubEndpoint() {
        return this.uri.toString();
    }

    /**
     * Gets retry limit.
     *
     * @return the retry limit
     */
    public long getRetryLimit() {
        return DEFAULT_RETRY_LIMIT;
    }

    /**
     * Gets retry wait time.
     *
     * @return the retry wait time
     */
    public long getRetryWaitTime() {
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
    public boolean isConnected() {
        return this.webSocket != null && this.webSocket.isOpen();
    }

    /**
     * Checks whether it is connecting.
     *
     * @return the boolean indicating whether it is connecting
     */
    public boolean isConnecting() {
        return this.webSocket != null && this.webSocket.isConnecting();
    }

    /**
     * Connect.
     */
    public void connect() {
        this.retryCount = 0;
        this.reconnect();
    }

    private void reconnect() {
        long retryLimit = this.getRetryLimit();
        if (retryLimit != RETRY_LIMIT_INFINITE && this.retryCount < retryLimit) {
            Log.i(TAG, String.format("Pubsub reconnection count > %d. Give up.", retryLimit));
            return;
        }

        if (this.isConnecting()) {
            long retryWaitTime = this.getBoundedRetryWaitTime();

            Log.i(TAG, String.format("Pubsub connecting, retry in %dms", retryWaitTime));
            this.delayReconnect(retryWaitTime);
            return;
        }

        if (!this.isConnected()) {
            Log.i(TAG, String.format(
                    "[RetryCount=%d] Pubsub Connecting to: %s",
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
        Log.i(TAG, String.format("Pubsub reconnect in %dms", delay));

        Context context = this.getContainer().getContext();
        android.os.Handler handler = new android.os.Handler(context.getMainLooper());

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Pubsub.this.reconnect();
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
    public Handler subscribe(String channel, Handler handler) {
        String trimmed = channel.trim();
        if (trimmed.length() == 0) {
            throw new InvalidParameterException("Cannot subscribe to empty channel name");
        }

        if (handler == null) {
            throw new InvalidParameterException("Missing subscription handler");
        }

        Set<Handler> channelHandlers = this.handlers.get(trimmed);
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
    public Handler[] unsubscribeAll(String channel) {
        String trimmed = channel.trim();
        if (trimmed.length() == 0) {
            throw new InvalidParameterException("Cannot unsubscribe to channel with empty name");
        }

        this.sendWebSocketUnsubscribe(trimmed);

        Set<Handler> channelHandlers = this.handlers.get(trimmed);
        if (channelHandlers != null) {
            this.handlers.remove(trimmed);

            Handler[] handlerArr = new Handler[channelHandlers.size()];
            channelHandlers.toArray(handlerArr);

            return handlerArr;
        }

        return new Handler[0];
    }

    /**
     * Unsubscribes a handler from a channel.
     *
     * @param channel the channel name
     * @param handler the handler
     * @return the handler being removed
     */
    public Handler unsubscribe(String channel, Handler handler) {
        String trimmed = channel.trim();
        if (trimmed.length() == 0) {
            throw new InvalidParameterException("Cannot unsubscribe to channel with empty name");
        }

        if (handler == null) {
            throw new InvalidParameterException("Missing subscription handler");
        }

        Set<Handler> channelHandlers = this.handlers.get(trimmed);
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
    public void publish(String channel, JSONObject data) {
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
        Log.i(TAG, String.format("Pubsub connection opened: %d %s", statusCode, statusMessage));
        this.retryCount = 0;

        Set<String> allChannels = this.handlers.keySet();
        for (String perChannel : allChannels) {
            this.sendWebSocketSubscribe(perChannel);
        }

        Queue<Message> pendingMessages = this.pendingMessages;
        this.pendingMessages = new LinkedList<>();

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

        Context context = this.getContainer().getContext();
        android.os.Handler handler;

        if (!this.handlerExecutionInBackground) {
            handler = new android.os.Handler(context.getMainLooper());
        } else {
            if (this.backgroundThread == null || this.backgroundThread.getLooper() == null) {
                throw new IllegalStateException("Background thread is not yet created");
            }

            handler = new android.os.Handler(this.backgroundThread.getLooper());
        }

        Set<Handler> channelHandlers = this.handlers.get(channel);
        if (channelHandlers != null) {
            for (final Handler perHandler : channelHandlers) {
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
    public void onError(WebSocketClientImpl.Exception exception) {
        Log.i(TAG, "Pubsub connection error: " + exception.getMessage());
    }

    @Override
    public void onClose(String reason) {
        Log.i(TAG, "Pubsub connection close: " + reason);
        this.delayReconnect(this.getBoundedRetryWaitTime());
    }

    /**
     * The Handler Interface.
     */
    public interface Handler {
        /**
         * The Handle Function.
         *
         * @param data the data
         */
        void handle(JSONObject data);
    }

    /**
     * The Pubsub Message.
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
         * Instantiates a new Pubsub Message.
         *
         * @param channel the pubsub channel
         * @param data    the pubsub data
         */
        Message(String channel, JSONObject data) {
            super();
            this.channel = channel;
            this.data = data;
        }
    }
}
