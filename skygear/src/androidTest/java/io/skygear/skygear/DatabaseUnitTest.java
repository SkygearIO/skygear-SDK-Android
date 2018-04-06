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
        instrumentationContainer = new Container(instrumentationContext, Configuration.testConfiguration());
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        instrumentationContext = null;
        instrumentationContainer = null;
    }

    @Test
    public void testPublicDatabaseNormalFlow() throws Exception {
        Database database = Database.Factory.publicDatabase(instrumentationContainer);

        assertEquals(instrumentationContainer, database.getContainer());
        assertEquals("_public", database.getName());
    }

    @Test
    public void testPrivateDatabaseNormalFlow() throws Exception {
        Database database = Database.Factory.privateDatabase(instrumentationContainer);

        assertEquals(instrumentationContainer, database.getContainer());
        assertEquals("_private", database.getName());
    }

    @Test(expected = InvalidParameterException.class)
    public void testDatabaseWeakReferenceToContainer() throws Exception {
        Container container = new Container(instrumentationContext, Configuration.testConfiguration());
        Database database = Database.Factory.publicDatabase(container);

        container = null;
        Runtime.getRuntime().gc();

        database.getContainer();
    }
}
