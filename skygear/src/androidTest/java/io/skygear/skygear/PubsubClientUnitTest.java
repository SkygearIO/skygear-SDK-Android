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
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class PubsubClientUnitTest {

    private abstract class WebSocketClientEmptyImpl implements WebSocketClient {
        @Override
        public void connect() {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public void sendMessage(String message) throws NotYetConnectedException {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public void onMessage(String message) {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public void cleanup() {
            // Do nothing
        }

        @Override
        public boolean isConnecting() {
            return false;
        }
    }

    static Context instrumentationContext;
    static Container instrumentationContainer;

    @BeforeClass
    public static void setUpClass() throws Exception {
        instrumentationContext = InstrumentationRegistry.getContext().getApplicationContext();
        Configuration config = new Configuration.Builder()
                .endPoint("http://skygear.dev/")
                .apiKey("changeme")
                .build();
        instrumentationContainer = new Container(instrumentationContext, config);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        instrumentationContext = null;
        instrumentationContainer = null;
    }

    @Test
    public void testPubsubCreationFlow() throws Exception {
        PubsubClient pubsubClient = new PubsubClient(instrumentationContainer);

        assertEquals("ws://skygear.dev/pubsub?api_key=changeme", pubsubClient.getPubsubEndpoint());
        assertNotNull(pubsubClient.webSocket);
    }

    @Test
    public void testPubsubConfigFlow() throws Exception {
        Configuration config = new Configuration.Builder()
                .endPoint("https://skygear.dev2/")
                .apiKey("dev2")
                .build();

        PubsubClient pubsubClient = new PubsubClient(instrumentationContainer);
        WebSocketClient oldWebSocket = pubsubClient.webSocket;

        pubsubClient.configure(config);

        assertEquals("wss://skygear.dev2/pubsub?api_key=dev2", pubsubClient.getPubsubEndpoint());
        assertTrue(oldWebSocket != pubsubClient.webSocket);
    }

    @Test(expected = InvalidParameterException.class)
    public void testPubsubNotAllowNonHttpEndpointFlow() throws Exception {
        Configuration config = new Configuration.Builder()
                .endPoint("ftp://skygear.dev2/")
                .apiKey("dev2")
                .build();

        PubsubClient pubsubClient = new PubsubClient(instrumentationContainer);
        pubsubClient.configure(config);
    }

    @Test
    public void testPubsubIsConnectedCheckFlow() throws Exception {
        final boolean[] checkpoints = new boolean[]{ false };
        WebSocketClient webSocketClient = new WebSocketClientEmptyImpl(){
            @Override
            public boolean isOpen() {
                checkpoints[0] = true;
                return true;
            }
        };

        PubsubClient pubsubClient = new PubsubClient(instrumentationContainer);
        pubsubClient.webSocket = webSocketClient;

        assertTrue(pubsubClient.isConnected());
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testPubsubSubscribeFlow() throws Exception {
        final boolean[] checkpoints = new boolean[]{ false, false };
        WebSocketClient webSocketClient = new WebSocketClientEmptyImpl(){
            @Override
            public boolean isOpen() {
                return true;
            }

            @Override
            public void sendMessage(String message) throws NotYetConnectedException {
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    assertEquals("sub", jsonObject.getString("action"));
                    assertEquals("test_channel_1", jsonObject.getString("channel"));

                    checkpoints[0] = true;
                } catch (JSONException e) {
                    fail(e.getMessage());
                }
            }
        };

        PubsubClient pubsubClient = new PubsubClient(instrumentationContainer);
        pubsubClient.webSocket = webSocketClient;

        PubsubHandler handler = new PubsubHandler() {
            @Override
            public void handle(JSONObject data) {
                fail("Should not run handle function");
            }
        };

        assertEquals(handler, pubsubClient.subscribe("test_channel_1", handler));
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testPubsubMultipleSubscriptionFlow() throws Exception {
        final int[] checkCounts = new int[]{ 0, 0 };
        WebSocketClient webSocketClient = new WebSocketClientEmptyImpl(){
            @Override
            public boolean isOpen() {
                return true;
            }

            @Override
            public void sendMessage(String message) throws NotYetConnectedException {
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    String channelName = jsonObject.getString("channel");
                    if (channelName.equals("test_channel_0")) {
                        checkCounts[0]++;
                    } else if (channelName.equals("test_channel_1")) {
                        checkCounts[1]++;
                    }
                } catch (JSONException e) {
                    fail(e.getMessage());
                }
            }
        };

        PubsubClient pubsubClient = new PubsubClient(instrumentationContainer);
        pubsubClient.webSocket = webSocketClient;

        pubsubClient.subscribe("test_channel_0", new PubsubHandler() {
            @Override
            public void handle(JSONObject data) {
                fail("Should not run handle function");
            }
        });
        assertEquals(1, checkCounts[0]);

        pubsubClient.subscribe("test_channel_0", new PubsubHandler() {
            @Override
            public void handle(JSONObject data) {
                fail("Should not run handle function");
            }
        });
        assertEquals(1, checkCounts[0]);

        pubsubClient.subscribe("test_channel_1", new PubsubHandler() {
            @Override
            public void handle(JSONObject data) {
                fail("Should not run handle function");
            }
        });
        assertEquals(1, checkCounts[1]);
    }

    @Test
    public void testPubsubCallHandlerOnMessageEvent() throws Exception {
        final boolean[] checkpoints = new boolean[]{ false, false, false };
        WebSocketClient webSocketClient = new WebSocketClientEmptyImpl(){
            @Override
            public boolean isOpen() {
                return true;
            }

            @Override
            public void sendMessage(String message) throws NotYetConnectedException {
                // Do nothing
            }
        };

        PubsubClient pubsubClient = new PubsubClient(instrumentationContainer);
        pubsubClient.webSocket = webSocketClient;

        final CountDownLatch latch0 = new CountDownLatch(2);
        final CountDownLatch latch1 = new CountDownLatch(1);
        pubsubClient.subscribe("test_channel_0", new PubsubHandler() {
            @Override
            public void handle(JSONObject data) {
                try {
                    assertEquals("test_msg_0", data.getString("msg"));
                    checkpoints[0] = true;
                    latch0.countDown();
                } catch (JSONException e) {
                    fail(e.getMessage());
                }
            }
        });

        pubsubClient.subscribe("test_channel_0", new PubsubHandler() {
            @Override
            public void handle(JSONObject data) {
                try {
                    assertEquals("test_msg_0", data.getString("msg"));
                    checkpoints[1] = true;
                    latch0.countDown();
                } catch (JSONException e) {
                    fail(e.getMessage());
                }
            }
        });

        pubsubClient.subscribe("test_channel_1", new PubsubHandler() {
            @Override
            public void handle(JSONObject data) {
                try {
                    assertEquals("test_msg_1", data.getString("msg"));
                    checkpoints[2] = true;
                    latch1.countDown();
                } catch (JSONException e) {
                    fail(e.getMessage());
                }
            }
        });

        pubsubClient.onMessage(new JSONObject(
                "{\"channel\": \"test_channel_0\",\"data\": {\"msg\": \"test_msg_0\"}}"
        ));
        latch0.await(1, TimeUnit.SECONDS);
        assertTrue(checkpoints[0]);
        assertTrue(checkpoints[1]);

        pubsubClient.onMessage(new JSONObject(
                "{\"channel\": \"test_channel_1\",\"data\": {\"msg\": \"test_msg_1\"}}"
        ));
        latch1.await(1, TimeUnit.SECONDS);
        assertTrue(checkpoints[2]);
    }

    @Test
    public void testPubsubUnsubscribeFlow() throws Exception {
        final boolean[] checkpoints = new boolean[]{ false, false };
        WebSocketClient webSocketClient = new WebSocketClientEmptyImpl(){
            @Override
            public boolean isOpen() {
                return true;
            }

            @Override
            public void sendMessage(String message) throws NotYetConnectedException {
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    if (jsonObject.getString("action").equals("unsub")) {
                        assertEquals("test_channel_1", jsonObject.getString("channel"));
                        checkpoints[0] = true;
                    }
                } catch (JSONException e) {
                    fail(e.getMessage());
                }
            }
        };

        PubsubClient pubsubClient = new PubsubClient(instrumentationContainer);
        pubsubClient.webSocket = webSocketClient;

        PubsubHandler handler = pubsubClient.subscribe("test_channel_1", new PubsubHandler() {
            @Override
            public void handle(JSONObject data) {
                fail("Should not run handle function");
            }
        });

        assertEquals(handler, pubsubClient.unsubscribe("test_channel_1", handler));
        assertTrue(checkpoints[0]);

        pubsubClient.onMessage(new JSONObject(
                "{\"channel\": \"test_channel_1\",\"data\": {\"msg\": \"test_msg_1\"}}"
        ));
    }

    @Test
    public void testPubsubNoUnsubscribeIfHandlerRegistered() throws Exception {
        final boolean[] checkpoints = new boolean[]{ false, false };
        WebSocketClient webSocketClient = new WebSocketClientEmptyImpl(){
            @Override
            public boolean isOpen() {
                return true;
            }

            @Override
            public void sendMessage(String message) throws NotYetConnectedException {
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    if (jsonObject.getString("action").equals("unsub")) {
                        assertEquals("test_channel_1", jsonObject.getString("channel"));
                        checkpoints[0] = true;
                    }
                } catch (JSONException e) {
                    fail(e.getMessage());
                }
            }
        };

        PubsubClient pubsubClient = new PubsubClient(instrumentationContainer);
        pubsubClient.webSocket = webSocketClient;

        PubsubHandler handler1 = pubsubClient.subscribe("test_channel_1", new PubsubHandler() {
            @Override
            public void handle(JSONObject data) {
                fail("Should not run handle function");
            }
        });
        PubsubHandler handler2 = pubsubClient.subscribe("test_channel_1", new PubsubHandler() {
            @Override
            public void handle(JSONObject data) {
                fail("Should not run handle function");
            }
        });

        pubsubClient.unsubscribe("test_channel_1", handler1);
        assertFalse(checkpoints[0]);

        pubsubClient.unsubscribe("test_channel_1", handler2);
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testPubsubUnsubscribeAllFlow() throws Exception {
        final boolean[] checkpoints = new boolean[]{ false, false };
        WebSocketClient webSocketClient = new WebSocketClientEmptyImpl(){
            @Override
            public boolean isOpen() {
                return true;
            }

            @Override
            public void sendMessage(String message) throws NotYetConnectedException {
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    if (jsonObject.getString("action").equals("unsub")) {
                        assertEquals("test_channel_1", jsonObject.getString("channel"));
                        checkpoints[0] = true;
                    }
                } catch (JSONException e) {
                    fail(e.getMessage());
                }
            }
        };

        PubsubClient pubsubClient = new PubsubClient(instrumentationContainer);
        pubsubClient.webSocket = webSocketClient;

        PubsubHandler handler1 = pubsubClient.subscribe("test_channel_1", new PubsubHandler() {
            @Override
            public void handle(JSONObject data) {
                fail("Should not run handle function");
            }
        });

        PubsubHandler handler2 = pubsubClient.subscribe("test_channel_1", new PubsubHandler() {
            @Override
            public void handle(JSONObject data) {
                fail("Should not run handle function");
            }
        });

        PubsubHandler[] handlers = pubsubClient.unsubscribeAll("test_channel_1");
        assertEquals(2, handlers.length);

        List<PubsubHandler> handlerList = Arrays.asList(handlers);
        assertTrue(handlerList.indexOf(handler1) != -1);
        assertTrue(handlerList.indexOf(handler2) != -1);

        assertTrue(checkpoints[0]);
    }

    @Test
    public void testPublishFlow() throws Exception {
        final boolean[] checkpoints = new boolean[]{ false, false };
        WebSocketClient webSocketClient = new WebSocketClientEmptyImpl(){
            @Override
            public boolean isOpen() {
                return true;
            }

            @Override
            public void sendMessage(String message) throws NotYetConnectedException {
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    assertEquals("pub", jsonObject.getString("action"));
                    assertEquals("test_channel_1", jsonObject.getString("channel"));

                    JSONObject data = jsonObject.getJSONObject("data");
                    assertEquals("test_msg_1", data.getString("msg"));

                    checkpoints[0] = true;
                } catch (JSONException e) {
                    fail(e.getMessage());
                }
            }
        };

        PubsubClient pubsubClient = new PubsubClient(instrumentationContainer);
        pubsubClient.webSocket = webSocketClient;

        pubsubClient.publish(
                "test_channel_1",
                new JSONObject("{\"msg\": \"test_msg_1\"}")
        );

        assertTrue(checkpoints[0]);
    }

    @Test(expected = InvalidParameterException.class)
    public void testPublishNullData() throws Exception {
        final boolean[] checkpoints = new boolean[]{ false, false };
        WebSocketClient webSocketClient = new WebSocketClientEmptyImpl(){
            @Override
            public boolean isOpen() {
                return true;
            }
        };

        PubsubClient pubsubClient = new PubsubClient(instrumentationContainer);
        pubsubClient.webSocket = webSocketClient;
        pubsubClient.publish("test_channel_1", null);
    }

    @Test
    public void testHandledSubscribeWhenNotConnected() throws Exception {
        WebSocketClient webSocketClient = new WebSocketClientEmptyImpl() {
            @Override
            public boolean isOpen() {
                return true;
            }

            @Override
            public void sendMessage(String message) throws NotYetConnectedException {
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    String action = jsonObject.getString("action");
                    if (action.equals("sub") || action.equals("unsub")) {
                        throw new NotYetConnectedException("Test Not Yet Connected Exception");
                    }

                    fail("Should not have other actions");
                } catch (JSONException e) {
                    fail(e.getMessage());
                }
            }
        };

        PubsubClient pubsubClient = new PubsubClient(instrumentationContainer);
        pubsubClient.webSocket = webSocketClient;

        pubsubClient.subscribe("HelloWorld", new PubsubHandler() {
            @Override
            public void handle(JSONObject data) {
                fail("Should not run handle function");
            }
        });
        assertEquals(1, pubsubClient.handlers.get("HelloWorld").size());

        pubsubClient.unsubscribeAll("HelloWorld");
        assertNull(pubsubClient.handlers.get("HelloWorld"));
    }

    @Test
    public void testResendSubscribeWhenConnectionOpen() throws Exception {
        final Map<String, Boolean> checkpoints = new HashMap<>();
        checkpoints.put("HelloWorld", false);
        checkpoints.put("FooBar", false);
        checkpoints.put("Haha-123", false);

        WebSocketClient emptyWebSocketClient = new WebSocketClientEmptyImpl() {
            @Override
            public boolean isOpen() {
                return true;
            }

            @Override
            public void sendMessage(String message) throws NotYetConnectedException {
                // Do nothing
            }
        };

        WebSocketClient checkingWebSocketClient = new WebSocketClientEmptyImpl() {
            @Override
            public boolean isOpen() {
                return true;
            }

            @Override
            public void sendMessage(String message) throws NotYetConnectedException {
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    if (jsonObject.getString("action").equals("sub")) {
                        checkpoints.put(jsonObject.getString("channel"), true);
                    }
                } catch (JSONException e) {
                    fail(e.getMessage());
                }
            }
        };

        PubsubClient pubsubClient = new PubsubClient(instrumentationContainer);
        pubsubClient.webSocket = emptyWebSocketClient;

        pubsubClient.subscribe("HelloWorld", new PubsubHandler() {
            @Override
            public void handle(JSONObject data) {
                fail("Should not run handle function");
            }
        });

        pubsubClient.subscribe("FooBar", new PubsubHandler() {
            @Override
            public void handle(JSONObject data) {
                fail("Should not run handle function");
            }
        });

        pubsubClient.subscribe("Haha-123", new PubsubHandler() {
            @Override
            public void handle(JSONObject data) {
                fail("Should not run handle function");
            }
        });

        pubsubClient.webSocket = checkingWebSocketClient;
        pubsubClient.onOpen(101, "Switching Protocols");

        assertTrue(checkpoints.get("HelloWorld"));
        assertTrue(checkpoints.get("FooBar"));
        assertTrue(checkpoints.get("Haha-123"));
    }

    @Test
    public void testQueueUpMessagesWhenNotConnected() throws Exception {
        WebSocketClient webSocketClient = new WebSocketClientEmptyImpl() {
            @Override
            public boolean isOpen() {
                return false;
            }

            @Override
            public void sendMessage(String message) throws NotYetConnectedException {
                // Do nothing
            }
        };

        PubsubClient pubsubClient = new PubsubClient(instrumentationContainer);
        pubsubClient.webSocket = webSocketClient;

        pubsubClient.publish(
                "test_channel",
                new JSONObject("{\"msg\": \"test_msg_1\"}")
        );
        pubsubClient.publish(
                "test_channel",
                new JSONObject("{\"msg\": \"test_msg_2\"}")
        );
        pubsubClient.publish(
                "test_channel",
                new JSONObject("{\"msg\": \"test_msg_3\"}")
        );

        Queue<PubsubClient.Message> pendingMessages = pubsubClient.pendingMessages;
        assertEquals(3, pendingMessages.size());

        PubsubClient.Message message1 = pendingMessages.remove();
        assertEquals("test_channel", message1.channel);
        assertEquals("{\"msg\":\"test_msg_1\"}", message1.data.toString());

        PubsubClient.Message message2 = pendingMessages.remove();
        assertEquals("test_channel", message2.channel);
        assertEquals("{\"msg\":\"test_msg_2\"}", message2.data.toString());

        PubsubClient.Message message3 = pendingMessages.remove();
        assertEquals("test_channel", message3.channel);
        assertEquals("{\"msg\":\"test_msg_3\"}", message3.data.toString());
    }

    @Test
    public void testResendPendingMessageWhenConnected() throws Exception {
        final Map<String, Boolean> checkpoints = new HashMap<>();
        checkpoints.put("test_msg_1", false);
        checkpoints.put("test_msg_2", false);
        checkpoints.put("test_msg_3", false);

        WebSocketClient disconnectedWebSocketClient = new WebSocketClientEmptyImpl() {
            @Override
            public boolean isOpen() {
                return false;
            }

            @Override
            public void sendMessage(String message) throws NotYetConnectedException {
                // Do nothing
            }
        };

        WebSocketClient connectedWebSocketClient = new WebSocketClientEmptyImpl() {
            @Override
            public boolean isOpen() {
                return true;
            }

            @Override
            public void sendMessage(String message) throws NotYetConnectedException {
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    if (jsonObject.getString("action").equals("pub") &&
                            jsonObject.getString("channel").equals("test_channel"))
                    {
                        checkpoints.put(jsonObject.getJSONObject("data").getString("msg"), true);
                    }
                } catch (JSONException e) {
                    fail(e.getMessage());
                }
            }
        };

        PubsubClient pubsubClient = new PubsubClient(instrumentationContainer);
        pubsubClient.webSocket = disconnectedWebSocketClient;

        pubsubClient.publish(
                "test_channel",
                new JSONObject("{\"msg\": \"test_msg_1\"}")
        );
        pubsubClient.publish(
                "test_channel",
                new JSONObject("{\"msg\": \"test_msg_2\"}")
        );
        pubsubClient.publish(
                "test_channel",
                new JSONObject("{\"msg\": \"test_msg_3\"}")
        );

        pubsubClient.webSocket = connectedWebSocketClient;
        pubsubClient.onOpen(101, "Switching Protocols");

        assertTrue(checkpoints.get("test_msg_1"));
        assertTrue(checkpoints.get("test_msg_2"));
        assertTrue(checkpoints.get("test_msg_3"));
    }
}
