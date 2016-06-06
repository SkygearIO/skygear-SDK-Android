package io.skygear.skygear;

import android.app.Application;
import android.content.Context;
import android.test.mock.MockApplication;
import android.test.mock.MockContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.security.InvalidParameterException;

import static org.junit.Assert.assertEquals;

public class ConfigurationUnitTest {
    Application mockApp;
    Context mockContext;

    @Before
    public void setUp() throws Exception {
        final Application mockApp = this.mockApp = new MockApplication();
        this.mockContext = new MockContext(){
            @Override
            public Context getApplicationContext() {
                return mockApp;
            }
        };
    }

    @After
    public void tearDown() throws Exception {
        this.mockApp = null;
        this.mockContext = null;
    }

    @Test
    public void testDefaultConfigurationNormalFlow() throws Exception {
        Configuration defaultConfig = Configuration.defaultConfiguration(this.mockContext);

        assertEquals("http://skygear.dev/", defaultConfig.endpoint);
        assertEquals("changeme", defaultConfig.apiKey);
        assertEquals(this.mockApp, defaultConfig.context);
    }

    @Test
    public void testConfigurationBuilderNormalFlow() throws Exception {
        Configuration config = new Configuration.Builder()
                .endPoint("http://my-endpoint.skygeario.com/")
                .apiKey("my-api-key")
                .context(this.mockContext)
                .build();

        assertEquals("http://my-endpoint.skygeario.com/", config.endpoint);
        assertEquals("my-api-key", config.apiKey);
        assertEquals(this.mockApp, config.context);
    }

    @Test(expected = InvalidParameterException.class)
    public void testConfigurationBuilderNotAllowNullEndpoint() throws Exception {
        new Configuration.Builder()
                .apiKey("my-api-key")
                .context(this.mockContext)
                .build();
    }

    @Test(expected = InvalidParameterException.class)
    public void testConfigurationBuilderNotAllowNullApiKey() throws Exception {
        new Configuration.Builder()
                .endPoint("http://my-endpoint.skygeario.com/")
                .context(this.mockContext)
                .build();
    }

    @Test(expected = InvalidParameterException.class)
    public void testConfigurationBuilderNotAllowNullContext() throws Exception {
        new Configuration.Builder()
                .endPoint("http://my-endpoint.skygeario.com/")
                .apiKey("my-api-key")
                .build();
    }
}
