package io.skygear.skygear;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.InvalidParameterException;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DatabaseUnitTest {
    static Context instrumentationContext;
    static Container instrumentationContainer;

    @BeforeClass
    public static void setUpClass() throws Exception {
        instrumentationContext = InstrumentationRegistry.getContext().getApplicationContext();
        instrumentationContainer = new Container(instrumentationContext, Configuration.defaultConfiguration());
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        instrumentationContext = null;
        instrumentationContainer = null;
    }

    @Test
    public void testPublicDatabaseNormalFlow() throws Exception {
        Database database = Database.publicDatabase(instrumentationContainer);

        assertEquals(instrumentationContainer, database.getContainer());
        assertEquals("_public", database.getName());
    }

    @Test
    public void testPrivateDatabaseNormalFlow() throws Exception {
        Database database = Database.privateDatabase(instrumentationContainer);

        assertEquals(instrumentationContainer, database.getContainer());
        assertEquals("_private", database.getName());
    }

    @Test(expected = InvalidParameterException.class)
    public void testDatabaseWeakReferenceToContainer() throws Exception {
        Container container = new Container(instrumentationContext, Configuration.defaultConfiguration());
        Database database = Database.publicDatabase(container);

        container = null;
        Runtime.getRuntime().gc();

        database.getContainer();
    }
}
