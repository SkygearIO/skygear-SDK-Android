package io.skygear.skygear;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The Skygear Pubsub.
 */
public class Pubsub implements WebSocketClientImpl.EventHandler {
    private static final String TAG = Pubsub.class.getSimpleName();
    final Map<String, Set<Handler>> handlers;

    private URI uri;
    private String apiKey;
    private WeakReference<Container> containerRef;

    WebSocketClient webSocket;

    /**
     * Instantiates a new Skygear Pubsub.
     * <p>
     *     Please be reminded that the skygear container passed in would be weakly referenced.
     * </p>
     *
     * @param container the skygear container
     */
    public Pubsub(Container container) {
        this.containerRef = new WeakReference<>(container);
        this.handlers = new HashMap<>();

        this.configure(container.getConfig());
    }

    /**
     * Updates the Configuration.
     * <p>
     *     Please be reminded that the connection will be re-initiate after the config update.
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

        this.webSocket = new WebSocketClientImpl(this.uri, this);
        this.webSocket.connect();
    }

    /**
     * Gets container.
     *
     * @return the container
     */
    public Container getContainer() {
        Container container = this.containerRef.get();
        if (container == null) {
            throw new InvalidParameterException("Missing container for database");
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
     * Checks whether it is connected.
     *
     * @return the boolean indicating whether it is connected
     */
    public boolean isConnected() {
        return this.webSocket.isOpen();
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
            try {
                JSONObject request = new JSONObject();
                request.put("action", "sub");
                request.put("channel", channel);

                this.webSocket.send(request.toString());
            } catch (JSONException e) {
                throw new InvalidParameterException("Invalid JSON format");
            }
        }

        // TODO: handle if not yet connected

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

        // TODO: handle if not yet connected
        try {
            JSONObject request = new JSONObject();
            request.put("action", "unsub");
            request.put("channel", channel);

            this.webSocket.send(request.toString());
        } catch (JSONException e) {
            throw new InvalidParameterException("Invalid JSON format");
        }

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

            // TODO: handle if not yet connected
            try {
                JSONObject request = new JSONObject();
                request.put("action", "unsub");
                request.put("channel", channel);

                this.webSocket.send(request.toString());
            } catch (JSONException e) {
                throw new InvalidParameterException("Invalid JSON format");
            }
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
        // TODO: handle if not yet connected

        String trimmed = channel.trim();
        if (trimmed.length() == 0) {
            throw new InvalidParameterException("Cannot publish event to channel with empty name");
        }

        try {
            JSONObject request = new JSONObject();
            request.put("action", "pub");
            request.put("channel", channel);
            if (data != null) {
                request.put("data", data);
            }

            this.webSocket.send(request.toString());
        } catch (JSONException e) {
            throw new InvalidParameterException("Invalid JSON format");
        }
    }

    @Override
    public void onOpen(int statusCode, String statusMessage) {
        Log.i(TAG, String.format(
                "onOpen: %d %s",
                statusCode,
                statusMessage
        ));
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
        android.os.Handler handler = new android.os.Handler(context.getMainLooper());

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
        Log.i(TAG, "onError: " + exception.getMessage());
    }

    @Override
    public void onClose(String reason) {
        Log.i(TAG, "onClose: " + reason);
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
}
