package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.InvalidParameterException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ConfigurationUnitTest {
    @Test
    public void testDefaultConfigurationNormalFlow() throws Exception {
        Configuration defaultConfig = Configuration.defaultConfiguration();

        assertEquals("http://skygear.dev/", defaultConfig.endpoint);
        assertEquals("changeme", defaultConfig.apiKey);
        assertNull(defaultConfig.gcmSenderId);
        assertFalse(defaultConfig.pubsubHandlerExecutionInBackground);
    }

    @Test
    public void testConfigurationBuilderNormalFlow() throws Exception {
        Configuration config = new Configuration.Builder()
                .endPoint("http://my-endpoint.skygeario.com/")
                .apiKey("my-api-key")
                .gcmSenderId("my-sender-id")
                .pubsubHandlerExecutionInBackground(true)
                .build();

        assertEquals("http://my-endpoint.skygeario.com/", config.endpoint);
        assertEquals("my-api-key", config.apiKey);
        assertEquals("my-sender-id", config.gcmSenderId);
        assertTrue(config.pubsubHandlerExecutionInBackground);
    }

    @Test(expected = InvalidParameterException.class)
    public void testConfigurationBuilderNotAllowNullEndpoint() throws Exception {
        new Configuration.Builder()
                .apiKey("my-api-key")
                .build();
    }

    @Test(expected = InvalidParameterException.class)
    public void testConfigurationBuilderNotAllowNullApiKey() throws Exception {
        new Configuration.Builder()
                .endPoint("http://my-endpoint.skygeario.com/")
                .build();
    }
}
