package io.skygear.skygear;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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

    @SuppressLint("CommitPrefEdits")
    private static void clearSharedPreferences(Context context) {
        SharedPreferences pref = context.getSharedPreferences(
                PersistentStore.SKYGEAR_PREF_SPACE,
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor edit = pref.edit();
        edit.clear();
        edit.commit();
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        instrumentationContext = InstrumentationRegistry.getContext().getApplicationContext();
        clearSharedPreferences(instrumentationContext);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        clearSharedPreferences(instrumentationContext);
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

    @Test
    public void testContainerPublicDatabase() throws Exception {
        Configuration config = Configuration.defaultConfiguration();
        Container container = new Container(instrumentationContext, config);
        Database publicDatabase = container.getPublicDatabase();

        assertEquals("_public", publicDatabase.getName());
        assertEquals(container, publicDatabase.getContainer());
    }

    @Test
    public void testContainerPrivateDatabase() throws Exception {
        Configuration config = Configuration.defaultConfiguration();
        Container container = new Container(instrumentationContext, config);
        User user = new User(
                "user_123",
                "token_123",
                "user123",
                "user123@skygear.dev"
        );
        container.resolveAuthUser(user);

        Database privateDatabase = container.getPrivateDatabase();

        assertEquals("_private", privateDatabase.getName());
        assertEquals(container, privateDatabase.getContainer());

        clearSharedPreferences(instrumentationContext);
    }

    @Test(expected = AuthenticationException.class)
    public void testContainerNotAllowGetPrivateDatabaseWithoutLogin() throws Exception {
        Configuration config = Configuration.defaultConfiguration();
        Container container = new Container(instrumentationContext, config);
        container.getPrivateDatabase();
    }
}
