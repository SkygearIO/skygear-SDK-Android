package io.skygear.skygear;

import android.util.Log;

import org.java_websocket.client.DefaultSSLWebSocketClientFactory;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * The WebSocket Client Implementation.
 */
class WebSocketClientImpl
        extends org.java_websocket.client.WebSocketClient
        implements WebSocketClient
{
    static private final String TAG = WebSocketClientImpl.class.getSimpleName();

    final WeakReference<EventHandler> eventHandler;

    /**
     * Instantiates a new WebSocket Client.
     *
     * @param serverURI    the server uri
     * @param eventHandler the event handler
     */
    WebSocketClientImpl(URI serverURI, EventHandler eventHandler) {
        super(serverURI, new Draft_17());
        this.eventHandler = new WeakReference<>(eventHandler);

        if (serverURI.getScheme().equalsIgnoreCase("wss")) {
            try {
                TrustManager[] trustManagers = new TrustManager[1];
                trustManagers[0] = new DummyX509TrustManager();

                SSLContext ctx = SSLContext.getInstance("TLS");
                ctx.init(null, trustManagers, new SecureRandom());

                this.setWebSocketFactory(new DefaultSSLWebSocketClientFactory(ctx));
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                Log.w(TAG, "WebSocketClientImpl: Fail to create SSL Context", e);
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Gets the event handler.
     *
     * @return the event handler
     */
    EventHandler getEventHandler() {
        return this.eventHandler.get();
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        EventHandler eventHandler = this.getEventHandler();
        if (eventHandler != null) {
            eventHandler.onOpen(
                    handshakedata.getHttpStatus(),
                    handshakedata.getHttpStatusMessage()
            );
        }
    }

    @Override
    public void sendMessage(String message) throws NotYetConnectedException {
        try {
            super.send(message);
        } catch (java.nio.channels.NotYetConnectedException e) {
            throw new NotYetConnectedException("Network not yet connected");
        } catch (WebsocketNotConnectedException e) {
            throw new NotYetConnectedException("WebSocket not yet connected");
        }
    }

    @Override
    public void onMessage(String message) {
        EventHandler eventHandler = this.getEventHandler();
        if (eventHandler != null) {
            try {
                eventHandler.onMessage(new JSONObject(message));
            } catch (JSONException e) {
                eventHandler.onError(new Exception(e.getMessage()));
            }
        }
    }

    @Override
    public void cleanup() {
        this.eventHandler.clear();
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        EventHandler eventHandler = this.getEventHandler();
        if (eventHandler != null) {
            this.getEventHandler().onClose(reason);
        }
    }

    @Override
    public void onError(java.lang.Exception ex) {
        EventHandler eventHandler = this.getEventHandler();
        if (eventHandler != null) {
            Exception exception = new Exception(ex.getMessage());
            this.getEventHandler().onError(exception);
        }
    }

    @Override
    public boolean isOpen() {
        return this.getConnection().isOpen();
    }

    @Override
    public boolean isConnecting() {
        return this.getConnection().isConnecting();
    }

    /**
     * The Event Handler Interface.
     */
    interface EventHandler {
        /**
         * The Connection Open Callback.
         *
         * @param statusCode    the status code
         * @param statusMessage the status message
         */
        void onOpen(int statusCode, String statusMessage);

        /**
         * The Message Receive Callback.
         *
         * @param eventData the event data
         */
        void onMessage(JSONObject eventData);

        /**
         * The Error Callback.
         *
         * @param exception the exception
         */
        void onError(Exception exception);

        /**
         * The Connection Close Callback.
         *
         * @param reason the reason
         */
        void onClose(String reason);
    }

    /**
     * The WebSocket Client Exception.
     */
    class Exception extends java.lang.Exception {
        /**
         * Instantiates a new Exception.
         *
         * @param detailMessage the detail message
         */
        public Exception(String detailMessage) {
            super(detailMessage);
        }
    }

    /**
     * The Dummy Implementation of x509 Trust Manager.
     */
    private static class DummyX509TrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
