package io.skygear.skygear;

import android.app.Application;
import android.content.Context;
import android.test.mock.MockApplication;
import android.test.mock.MockContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ContainerUnitTest {
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
    public void testContainerNormalFlow() throws Exception {
        Configuration config = Configuration.defaultConfiguration(this.mockContext);
        Container container = new Container(config);

        assertEquals(config, container.getConfig());
    }

    @Test
    public void testDefaultContainerNormalFlow() throws Exception {
        Configuration config = Configuration.defaultConfiguration(this.mockContext);
        Container container = Container.defaultContainer(this.mockContext);

        assertEquals(config.endpoint, container.getConfig().endpoint);
        assertEquals(config.apiKey, container.getConfig().apiKey);
        assertEquals(this.mockApp, container.getConfig().context);
    }

    @Test
    public void testDefaultContainerIsSingleton() throws Exception {
        Container container1 = Container.defaultContainer(this.mockContext);
        Container container2 = Container.defaultContainer(this.mockContext);

        assertEquals(container2, container1);
    }

    @Test
    public void testContainerUpdateConfiguration() throws Exception {
        Configuration config1 = new Configuration.Builder()
                .endPoint("http://my-endpoint.skygeario.com")
                .apiKey("my-api-key")
                .context(this.mockContext)
                .build();
        Configuration config2 = new Configuration.Builder()
                .endPoint("http://my-endpoint-2.skygeario.com")
                .apiKey("my-api-key-2")
                .context(this.mockContext)
                .build();
        Container container = new Container(config1);
        container.configure(config2);

        assertEquals(config2, container.getConfig());
    }
}
