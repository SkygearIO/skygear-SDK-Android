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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

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
        Configuration config = Configuration.testConfiguration();
        Container container = new Container(instrumentationContext, config);

        assertEquals(config, container.getConfig());
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
        Configuration config = Configuration.testConfiguration();
        Container container = new Container(instrumentationContext, config);
        Database publicDatabase = container.getPublicDatabase();

        assertEquals("_public", publicDatabase.getName());
        assertEquals(container, publicDatabase.getContainer());
    }

    @Test
    public void testContainerPrivateDatabase() throws Exception {
        Configuration config = Configuration.testConfiguration();
        Container container = new Container(instrumentationContext, config);
        Map profile =  new HashMap<>();
        profile.put("username", "user123");
        profile.put("email", "user123@skygear.dev");

        Record user = new Record("user", profile);
        container.getAuth().resolveAuthUser(user, "token_123");

        Database privateDatabase = container.getPrivateDatabase();

        assertEquals("_private", privateDatabase.getName());
        assertEquals(container, privateDatabase.getContainer());

        clearSharedPreferences(instrumentationContext);
    }

    @Test(expected = AuthenticationException.class)
    public void testContainerNotAllowGetPrivateDatabaseWithoutLogin() throws Exception {
        Configuration config = Configuration.testConfiguration();
        Container container = new Container(instrumentationContext, config);
        container.getPrivateDatabase();
    }
}
