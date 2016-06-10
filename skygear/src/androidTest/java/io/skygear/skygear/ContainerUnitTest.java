package io.skygear.skygear;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ContainerUnitTest {
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
    public void testContainerNormalFlow() throws Exception {
        Configuration config = Configuration.defaultConfiguration();
        Container container = new Container(instrumentationContext, config);

        assertEquals(config, container.getConfig());
    }

    @Test
    public void testDefaultContainerNormalFlow() throws Exception {
        Configuration config = Configuration.defaultConfiguration();
        Container container = Container.defaultContainer(instrumentationContext);

        assertEquals(config.endpoint, container.getConfig().endpoint);
        assertEquals(config.apiKey, container.getConfig().apiKey);
        assertEquals(instrumentationContext, container.getContext());
    }

    @Test
    public void testDefaultContainerIsSingleton() throws Exception {
        Container container1 = Container.defaultContainer(instrumentationContext);
        Container container2 = Container.defaultContainer(instrumentationContext);

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
        Container container = new Container(instrumentationContext, config1);
        container.configure(config2);

        assertEquals(config2, container.getConfig());
    }
}
