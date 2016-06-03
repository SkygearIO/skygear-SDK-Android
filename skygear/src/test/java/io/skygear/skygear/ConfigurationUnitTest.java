package io.skygear.skygear;

import android.content.Context;
import android.test.mock.MockContext;

import org.junit.Test;

import java.security.InvalidParameterException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ConfigurationUnitTest {
    @Test
    public void testDefaultConfigurationNormalFlow() throws Exception {
        Configuration defaultConfig = Configuration.defaultConfiguration();

        assertEquals("http://skygear.dev/", defaultConfig.endpoint);
        assertEquals("changeme", defaultConfig.apiKey);
        assertNull(defaultConfig.context);
    }

    @Test
    public void testConfigurationBuilderNormalFlow() throws Exception {
        Context mockContext = new MockContext();
        Configuration config = new Configuration.Builder()
                .endPoint("http://my-endpoint.skygeario.com/")
                .apiKey("my-api-key")
                .context(mockContext)
                .build();

        assertEquals("http://my-endpoint.skygeario.com/", config.endpoint);
        assertEquals("my-api-key", config.apiKey);
        assertEquals(mockContext, config.context);
    }

    @Test(expected = InvalidParameterException.class)
    public void testConfigurationBuilderNotAllowNullEndpoint() throws Exception {
        new Configuration.Builder()
                .apiKey("my-api-key")
                .context(new MockContext())
                .build();
    }

    @Test(expected = InvalidParameterException.class)
    public void testConfigurationBuilderNotAllowNullApiKey() throws Exception {
        new Configuration.Builder()
                .endPoint("http://my-endpoint.skygeario.com/")
                .context(new MockContext())
                .build();
    }

    @Test
    public void testConfigurationBuilderAllowNullContext() throws Exception {
        new Configuration.Builder()
                .endPoint("http://my-endpoint.skygeario.com/")
                .apiKey("my-api-key")
                .build();
    }
}
