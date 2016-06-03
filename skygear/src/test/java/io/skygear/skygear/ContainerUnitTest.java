package io.skygear.skygear;

import android.test.mock.MockContext;

import org.junit.Test;

import java.security.InvalidParameterException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ContainerUnitTest {
    @Test
    public void testContainerNormalFlow() throws Exception {
        Configuration config = Configuration.defaultConfiguration();
        Container container = new Container(config);

        assertEquals(config, container.getConfig());
    }

    @Test
    public void testDefaultContainerNormalFlow() throws Exception {
        Configuration config = Configuration.defaultConfiguration();
        Container container = Container.defaultContainer();

        assertEquals(config.endpoint, container.getConfig().endpoint);
        assertEquals(config.apiKey, container.getConfig().apiKey);
        assertNull(container.getConfig().context);
    }

    @Test
    public void testDefaultContainerIsSingleton() throws Exception {
        Container container1 = Container.defaultContainer();
        Container container2 = Container.defaultContainer();

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
                .context(new MockContext())
                .build();
        Container container = new Container(config1);
        container.configure(config2);

        assertEquals(config2, container.getConfig());
    }

    @Test(expected = InvalidParameterException.class)
    public void testContainerNowAllowUpdateNullConfiguration() throws Exception {
        Configuration config = new Configuration.Builder()
                .endPoint("http://my-endpoint.skygeario.com")
                .apiKey("my-api-key")
                .build();
        Container container = new Container(config);
        container.configure(null);
    }
}
