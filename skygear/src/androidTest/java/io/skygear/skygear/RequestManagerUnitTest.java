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

import com.android.volley.AuthFailureError;
import io.skygear.utils.volley.MultiPartParam;
import io.skygear.utils.volley.MultiPartRequest;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;


@RunWith(AndroidJUnit4.class)
public class RequestManagerUnitTest {
    static Context instrumentationContext;

    @BeforeClass
    public static void setUpClass() throws Exception {
        instrumentationContext = InstrumentationRegistry.getContext().getApplicationContext();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        instrumentationContext = null;
    }

    @Test
    public void testRequestManagerNormalFlow() throws Exception {
        Configuration config = Configuration.testConfiguration();
        RequestManager requestManager = new RequestManager(
                RequestManagerUnitTest.instrumentationContext,
                config
        );

        assertEquals(config.endpoint, requestManager.endpoint);
        assertEquals(config.apiKey, requestManager.apiKey);
        assertNotNull(requestManager.queue);
    }

    @Test
    public void testRequestManagerUpdateConfig() throws Exception {
        RequestManager requestManager = new RequestManager(
                RequestManagerUnitTest.instrumentationContext,
                Configuration.testConfiguration()
        );

        Configuration config = new Configuration.Builder()
                .endPoint("http://my-endpoint.skygeario.com/")
                .apiKey("my-api-key")
                .build();

        requestManager.configure(config);

        assertEquals(config.endpoint, requestManager.endpoint);
        assertEquals(config.apiKey, requestManager.apiKey);
    }

    @Test
    public void testSendRequestNormalFlow() throws Exception {
        Configuration config = new Configuration.Builder()
                .endPoint("http://my-endpoint.skygeario.com/")
                .apiKey("my-api-key")
                .build();
        RequestManager requestManager = new RequestManager(
                RequestManagerUnitTest.instrumentationContext,
                config
        );

        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] checkpoints = { false };

        HttpStack httpStack = new MockHttpStack(new MockHttpStack.RequestValidator() {
            @Override
            public void validate(com.android.volley.Request request, Map<String, String> additionalHeaders)
                    throws AuthFailureError
            {
                assertEquals("http://my-endpoint.skygeario.com/test/action", request.getUrl());
                assertEquals("application/json", request.getBodyContentType());

                Map headers = request.getHeaders();
                assertEquals("my-api-key", (String)headers.get("X-Skygear-API-Key"));

                try {
                    JSONObject bodyObject = new JSONObject(new String(request.getBody()));

                    assertEquals("test:action", bodyObject.getString("action"));
                    assertEquals("my-api-key", bodyObject.getString("api_key"));
                    assertEquals("world", bodyObject.getString("hello"));
                    assertEquals("bar", bodyObject.getString("foo"));
                } catch (JSONException e) {
                    fail("Invalid body format");
                }


            }
        });

        requestManager.queue = Volley.newRequestQueue(
                RequestManagerUnitTest.instrumentationContext,
                httpStack
        );

        Map<String, Object> data = new HashMap<>();
        data.put("hello", "world");
        data.put("foo", "bar");

        ResponseHandler responseHandler = new ResponseHandler() {
            @Override
            public void onSuccess(JSONObject result) {
                checkpoints[0] = true;
                latch.countDown();
            }

            @Override
            public void onFail(Error error) {
                fail("Should not get error callback");
            }
        };

        Request request = new Request("test:action", data, responseHandler);
        requestManager.sendRequest(request);

        latch.await(1, TimeUnit.SECONDS);
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testSendRequestWithAccessToken() throws Exception {
        RequestManager requestManager = new RequestManager(
                RequestManagerUnitTest.instrumentationContext,
                Configuration.testConfiguration()
        );

        requestManager.accessToken = "my-access-token";

        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] checkpoints = { false };

        HttpStack httpStack = new MockHttpStack(new MockHttpStack.RequestValidator() {
            @Override
            public void validate(com.android.volley.Request request, Map<String, String> additionalHeaders)
                    throws AuthFailureError
            {
                Map headers = request.getHeaders();
                assertEquals("my-access-token", (String)headers.get("X-Skygear-Access-Token"));

                try {
                    JSONObject bodyObject = new JSONObject(new String(request.getBody()));
                    assertEquals("my-access-token", bodyObject.getString("access_token"));
                } catch (JSONException e) {
                    fail("Invalid body format");
                }
            }
        });

        requestManager.queue = Volley.newRequestQueue(
                RequestManagerUnitTest.instrumentationContext,
                httpStack
        );

        ResponseHandler responseHandler = new ResponseHandler() {
            @Override
            public void onSuccess(JSONObject result) {
                checkpoints[0] = true;
                latch.countDown();
            }

            @Override
            public void onFail(Error error) {
                fail("Should not get error callback");
            }
        };

        Request request = new Request("test:action", new HashMap<String, Object>(), responseHandler);
        requestManager.sendRequest(request);

        latch.await(1, TimeUnit.SECONDS);
        assertTrue(checkpoints[0]);

    }

    @Test
    public void testSendRequestWithHandleSuccessResponse() throws Exception {
        RequestManager requestManager = new RequestManager(
                RequestManagerUnitTest.instrumentationContext,
                Configuration.testConfiguration()
        );

        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] checkpoints = { false };

        HttpStack httpStack = new MockHttpStack(new MockHttpStack.MockResponder() {
            @Override
            public HttpResponse getResponse(com.android.volley.Request request, Map<String, String> header)
                    throws AuthFailureError
            {
                BasicHttpResponse response = new BasicHttpResponse(
                        new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK")
                );

                try {
                    response.setEntity(
                            new StringEntity("{\"result\": {\"status\": \"OK\"}}")
                    );
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                return response;
            }
        });

        requestManager.queue = Volley.newRequestQueue(
                RequestManagerUnitTest.instrumentationContext,
                httpStack
        );

        ResponseHandler responseHandler = new ResponseHandler() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    assertEquals("OK", result.getString("status"));
                } catch (JSONException e) {
                    fail("Invalid response format");
                }

                checkpoints[0] = true;
                latch.countDown();
            }

            @Override
            public void onFail(Error error) {
                fail("Should not get error callback");
            }
        };

        Request request = new Request("test:action", new HashMap<String, Object>(), responseHandler);
        requestManager.sendRequest(request);

        latch.await(1, TimeUnit.SECONDS);
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testSendRequestWithHandleErrorResponse() throws Exception {
        RequestManager requestManager = new RequestManager(
                RequestManagerUnitTest.instrumentationContext,
                Configuration.testConfiguration()
        );

        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] checkpoints = { false };

        HttpStack httpStack = new MockHttpStack(new MockHttpStack.MockResponder() {
            @Override
            public HttpResponse getResponse(com.android.volley.Request request, Map<String, String> header)
                    throws AuthFailureError
            {
                BasicHttpResponse response = new BasicHttpResponse(
                        new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 401, "Unauthorized")
                );

                try {
                    response.setEntity(
                            new StringEntity(
                                    "{" +
                                    "  \"error\": {" +
                                    "    \"name\": \"PermissionDenied\", " +
                                    "    \"code\": 102," +
                                    "    \"message\": \"write is not allowed\"," +
                                    "    \"info\": {\"foo\":\"bar\"}" +
                                    "  }" +
                                    "}"
                            )
                    );
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                return response;
            }
        });

        requestManager.queue = Volley.newRequestQueue(
                RequestManagerUnitTest.instrumentationContext,
                httpStack
        );

        ResponseHandler responseHandler = new ResponseHandler() {
            @Override
            public void onSuccess(JSONObject result) {
                fail("Should not get success callback");
            }

            @Override
            public void onFail(Error error) {
                assertEquals("write is not allowed", error.getDetailMessage());
                assertEquals("PermissionDenied", error.getName());
                assertEquals(Error.Code.PERMISSION_DENIED, error.getCode());
                try {
                    assertEquals("bar", error.getInfo().getString("foo"));
                } catch (JSONException e) {
                    fail(e.getMessage());
                }
                checkpoints[0] = true;
                latch.countDown();
            }
        };

        Request request = new Request("test:action", new HashMap<String, Object>(), responseHandler);
        requestManager.sendRequest(request);

        latch.await(1, TimeUnit.SECONDS);
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testRequestValidationSuccess() throws Exception {
        RequestManager requestManager = new RequestManager(
                RequestManagerUnitTest.instrumentationContext,
                Configuration.testConfiguration()
        );
        requestManager.queue = Volley.newRequestQueue(
                RequestManagerUnitTest.instrumentationContext,
                new MockHttpStack()
        );

        final CountDownLatch latch = new CountDownLatch(2);
        final boolean[] checkpoints = {false, false};

        ResponseHandler responseHandler = new ResponseHandler() {
            @Override
            public void onSuccess(JSONObject result) {
                checkpoints[1] = true;
                latch.countDown();
            }

            @Override
            public void onFail(Error error) {
                fail("Should not get error callback");
            }
        };

        Request request = new Request(
                "test:action",
                new HashMap<String, Object>(),
                responseHandler
        ) {
            @Override
            protected void validate() throws Exception {
                checkpoints[0] = true;
                latch.countDown();
            }
        };

        requestManager.sendRequest(request);

        latch.await(1, TimeUnit.SECONDS);

        assertTrue(checkpoints[0]);
        assertTrue(checkpoints[1]);
    }

    @Test
    public void testRequestValidationFail() throws Exception {
        RequestManager requestManager = new RequestManager(
                RequestManagerUnitTest.instrumentationContext,
                Configuration.testConfiguration()
        );
        requestManager.queue = Volley.newRequestQueue(
                RequestManagerUnitTest.instrumentationContext,
                new MockHttpStack()
        );

        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] checkpoints = { false };

        ResponseHandler responseHandler = new ResponseHandler() {
            @Override
            public void onSuccess(JSONObject result) {
                fail("Should not get success callback");
            }

            @Override
            public void onFail(Error error) {
                assertEquals("Test validation exception", error.getDetailMessage());

                checkpoints[0] = true;
                latch.countDown();
            }
        };

        Request request = new Request(
                "test:action",
                new HashMap<String, Object>(),
                responseHandler
        ) {
            @Override
            protected void validate() throws Exception {
                throw new Exception("Test validation exception");
            }
        };

        requestManager.sendRequest(request);

        latch.await(1, TimeUnit.SECONDS);
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testSendAssetPostRequest() throws Exception {
        RequestManager requestManager = new RequestManager(
                RequestManagerUnitTest.instrumentationContext,
                Configuration.testConfiguration()
        );
        requestManager.queue = Volley.newRequestQueue(
                RequestManagerUnitTest.instrumentationContext,
                new MockHttpStack(new MockHttpStack.RequestValidator() {
                    @Override
                    public void validate(
                            com.android.volley.Request request,
                            Map<String, String> additionalHeaders
                    ) throws AuthFailureError {
                        assertTrue(request instanceof MultiPartRequest);
                        MultiPartRequest multiPartRequest = (MultiPartRequest) request;

                        Map<String, MultiPartParam> multipartParams
                                = multiPartRequest.getMultipartParams();
                        assertEquals("world", multipartParams.get("hello").value);
                        assertEquals("bar", multipartParams.get("foo").value);

                        assertTrue(multiPartRequest.getFilesToUpload().keySet().contains("file"));
                    }
                })
        );

        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] checkpoints = { false };

        Asset asset = new Asset("hello.txt", "text/plain", "hello world".getBytes());
        Map<String, String> extraFields = new HashMap<>();
        extraFields.put("hello", "world");
        extraFields.put("foo", "bar");

        AssetPostRequest request = new AssetPostRequest(
                asset,
                "http://skygear.dev/asset/upload",
                extraFields
        );
        request.responseHandler = new AssetPostRequest.ResponseHandler() {
            @Override
            public void onPostSuccess(Asset asset, String response) {
                checkpoints[0] = true;
                latch.countDown();
            }

            @Override
            public void onPostFail(Asset asset, Error error) {
                fail("Should not get fail callback");
            }
        };

        requestManager.sendAssetPostRequest(request);

        latch.await();
        assertTrue(checkpoints[0]);
    }
}
