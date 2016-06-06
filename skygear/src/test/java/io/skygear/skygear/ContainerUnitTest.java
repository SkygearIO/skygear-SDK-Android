package io.skygear.skygear;

import android.app.Application;
import android.content.Context;
import android.test.mock.MockApplication;
import android.test.mock.MockContext;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ContainerUnitTest {
    static Application mockApp;
    static Context mockContext;

    @BeforeClass
    public static void setUpClass() throws Exception {
        final Context mockApp = ContainerUnitTest.mockApp = new MockApplication();
        ContainerUnitTest.mockContext = new MockContext(){
            @Override
            public Context getApplicationContext() {
                return mockApp;
            }
        };
    }

    @AfterClass
    public static void tearDown() throws Exception {
        ContainerUnitTest.mockApp = null;
        ContainerUnitTest.mockContext = null;
    }

    @Test
    public void testContainerNormalFlow() throws Exception {
        Configuration config = Configuration.defaultConfiguration();
        Container container = new Container(ContainerUnitTest.mockContext, config);

        assertEquals(config, container.getConfig());
    }

    @Test
    public void testDefaultContainerNormalFlow() throws Exception {
        Configuration config = Configuration.defaultConfiguration();
        Container container = Container.defaultContainer(ContainerUnitTest.mockContext);

        assertEquals(config.endpoint, container.getConfig().endpoint);
        assertEquals(config.apiKey, container.getConfig().apiKey);
        assertEquals(ContainerUnitTest.mockApp, container.getContext());
    }

    @Test
    public void testDefaultContainerIsSingleton() throws Exception {
        Container container1 = Container.defaultContainer(ContainerUnitTest.mockContext);
        Container container2 = Container.defaultContainer(ContainerUnitTest.mockContext);

        assertEquals(container2, container1);
    }

    @Test
    public void testContainerUpdateConfiguration() throws Exception {
        Configuration config1 = new Configuration.Builder()
                .endPoint("http://my-endpoint.skygeario.com")
                .apiKey("my-api-key")
                .build();
        Configuration config2 = new Configuration.Builder()
                .endPoint("http://my-endpoint-2.skygeario.com")
                .apiKey("my-api-key-2")
                .build();
        Container container = new Container(ContainerUnitTest.mockContext, config1);
        container.configure(config2);

        assertEquals(config2, container.getConfig());
    }
}
