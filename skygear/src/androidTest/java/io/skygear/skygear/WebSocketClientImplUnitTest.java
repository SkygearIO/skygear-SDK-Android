package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URI;
import java.util.Iterator;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class WebSocketClientImplUnitTest {

    private class ServerHandshakeEmptyImpl implements ServerHandshake {
        @Override
        public short getHttpStatus() {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public String getHttpStatusMessage() {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public Iterator<String> iterateHttpFields() {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public String getFieldValue(String name) {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public boolean hasFieldValue(String name) {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public byte[] getContent() {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    private class WebSocketEventHandlerEmptyImpl implements WebSocketClientImpl.EventHandler {
        @Override
        public void onOpen(int statusCode, String statusMessage) {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public void onMessage(JSONObject eventData) {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public void onError(WebSocketClientImpl.Exception exception) {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public void onClose(String reason) {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    @Test
    public void testWebSocketClientImplOpenFlow() throws Exception {
        final boolean[] checkpoints = new boolean[]{ false };
        WebSocketClientImpl.EventHandler eventHandler = new WebSocketEventHandlerEmptyImpl() {
            @Override
            public void onOpen(int statusCode, String statusMessage) {
                assertEquals(200, statusCode);
                assertEquals("OK", statusMessage);

                checkpoints[0] = true;
            }
        };

        WebSocketClientImpl client = new WebSocketClientImpl(
                URI.create("ws://skygear.dev/pubsub?api_key=123"),
                eventHandler
        );

        client.onOpen(new ServerHandshakeEmptyImpl(){
            @Override
            public short getHttpStatus() {
                return 200;
            }

            @Override
            public String getHttpStatusMessage() {
                return "OK";
            }
        });
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testWebSocketClientImplMessageFlow() throws Exception {
        final boolean[] checkpoints = new boolean[]{ false };
        WebSocketClientImpl.EventHandler eventHandler = new WebSocketEventHandlerEmptyImpl() {
            @Override
            public void onMessage(JSONObject eventData) {
                try {
                    assertEquals("world", eventData.getString("hello"));
                    assertEquals(123, eventData.getInt("foobar"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }

                checkpoints[0] = true;
            }
        };

        WebSocketClientImpl client = new WebSocketClientImpl(
                URI.create("ws://skygear.dev/pubsub?api_key=123"),
                eventHandler
        );

        client.onMessage("{\"hello\": \"world\", \"foobar\": 123}");
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testWebSocketClientImplCloseFlow() throws Exception {
        final boolean[] checkpoints = new boolean[]{ false };
        WebSocketClientImpl.EventHandler eventHandler = new WebSocketEventHandlerEmptyImpl() {
            @Override
            public void onClose(String reason) {
                assertEquals("Test Reason", reason);
                checkpoints[0] = true;
            }
        };

        WebSocketClientImpl client = new WebSocketClientImpl(
                URI.create("ws://skygear.dev/pubsub?api_key=123"),
                eventHandler
        );

        client.onClose(123, "Test Reason", false);
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testWebSocketClientImplErrorFlow() throws Exception {
        final boolean[] checkpoints = new boolean[]{ false };
        WebSocketClientImpl.EventHandler eventHandler = new WebSocketEventHandlerEmptyImpl() {
            @Override
            public void onError(WebSocketClientImpl.Exception exception) {
                assertEquals("Test Exception", exception.getMessage());
                checkpoints[0] = true;
            }
        };

        WebSocketClientImpl client = new WebSocketClientImpl(
                URI.create("ws://skygear.dev/pubsub?api_key=123"),
                eventHandler
        );

        client.onError(new Exception("Test Exception"));
        assertTrue(checkpoints[0]);
    }
}
