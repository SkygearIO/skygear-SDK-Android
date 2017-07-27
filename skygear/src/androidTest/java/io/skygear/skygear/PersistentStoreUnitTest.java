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

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@SuppressLint("CommitPrefEdits")
public class PersistentStoreUnitTest {
    static Context instrumentationContext;

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

    @Before
    public void setUp() throws Exception {
        clearSharedPreferences(instrumentationContext);
    }

    @After
    public void tearDown() throws Exception {
        clearSharedPreferences(instrumentationContext);
    }

    @Test
    public void testPersistentStoreRestoreUser() throws Exception {
        SharedPreferences pref = instrumentationContext.getSharedPreferences(
                PersistentStore.SKYGEAR_PREF_SPACE,
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(
                PersistentStore.CURRENT_USER_KEY,
                "{" +
                        "\"_id\": \"user/123\"," +
                        "\"username\": \"user_123\"," +
                        "\"email\": \"user123@skygear.dev\"" +
                "}"
        );
        editor.putString(PersistentStore.ACCESS_TOKEN_KEY, "token_123");
        editor.commit();

        PersistentStore persistentStore = new PersistentStore(instrumentationContext);
        Record currentUser = persistentStore.currentUser;

        assertEquals("user", currentUser.type);
        assertEquals("123", currentUser.id);
        assertEquals("user_123", currentUser.get("username"));
        assertEquals("user123@skygear.dev", currentUser.get("email"));

        assertEquals("token_123", persistentStore.accessToken);
    }

    @Test
    public void testPersistentStoreRestoreUserFromEmptyState() throws Exception {
        PersistentStore persistentStore = new PersistentStore(instrumentationContext);
        assertNull(persistentStore.currentUser);
    }

    @Test
    public void testPersistentStoreSaveUser() throws Exception {
        PersistentStore persistentStore = new PersistentStore(instrumentationContext);
        Map profile = new HashMap<>();
        profile.put("username", "user_12345");
        profile.put("email", "user12345@skygear.dev");

        persistentStore.currentUser = new Record("user", "12345", profile);
        persistentStore.accessToken = "token_12345";
        persistentStore.save();

        SharedPreferences pref = instrumentationContext.getSharedPreferences(
                PersistentStore.SKYGEAR_PREF_SPACE,
                Context.MODE_PRIVATE
        );

        String currentUserString = pref.getString(PersistentStore.CURRENT_USER_KEY, "{}");
        JSONObject currentUserJson = new JSONObject(currentUserString);

        assertEquals("user/12345", currentUserJson.getString("_id"));
        assertEquals("user_12345", currentUserJson.getString("username"));
        assertEquals("user12345@skygear.dev", currentUserJson.getString("email"));
        assertEquals("token_12345", pref.getString(PersistentStore.ACCESS_TOKEN_KEY, null));
    }

    @Test
    public void testPersistentStoreSaveNullUser() throws Exception {
        PersistentStore persistentStore = new PersistentStore(instrumentationContext);
        persistentStore.currentUser = null;
        persistentStore.save();

        SharedPreferences pref = instrumentationContext.getSharedPreferences(
                PersistentStore.SKYGEAR_PREF_SPACE,
                Context.MODE_PRIVATE
        );
        assertEquals("{}", pref.getString(PersistentStore.CURRENT_USER_KEY, "{}"));
    }

    @Test
    public void testPersistentStoreRestoreDefaultAccessControl() throws Exception {
        SharedPreferences pref = instrumentationContext.getSharedPreferences(
                PersistentStore.SKYGEAR_PREF_SPACE,
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(
                PersistentStore.DEFAULT_ACCESS_CONTROL_KEY,
                "[{" +
                    "\"public\": true," +
                    "\"level\": \"write\"" +

                "}]"
        );
        editor.commit();

        PersistentStore persistentStore = new PersistentStore(instrumentationContext);
        AccessControl defaultAccessControl = persistentStore.defaultAccessControl;
        assertEquals(AccessControl.Level.READ_WRITE, defaultAccessControl.getPublicAccess().getLevel());
    }

    @Test
    public void testPersistentStoreRestoreDefaultAccessControlFromEmptyState() throws Exception {
        PersistentStore persistentStore = new PersistentStore(instrumentationContext);
        assertEquals(
                AccessControl.Level.READ_ONLY,
                persistentStore.defaultAccessControl.getPublicAccess().getLevel()
        );
    }

    @Test
    public void testPersistentStoreSaveDefaultAccessControl() throws Exception {
        PersistentStore persistentStore = new PersistentStore(instrumentationContext);
        persistentStore.defaultAccessControl = new AccessControl(new AccessControl.Entry[]{
                new AccessControl.Entry(AccessControl.Level.READ_WRITE)
        });
        persistentStore.save();

        SharedPreferences pref = instrumentationContext.getSharedPreferences(
                PersistentStore.SKYGEAR_PREF_SPACE,
                Context.MODE_PRIVATE
        );

        String defaultAccessControlString
                = pref.getString(PersistentStore.DEFAULT_ACCESS_CONTROL_KEY, "[]");
        JSONArray defaultAccessControlJson = new JSONArray(defaultAccessControlString);

        assertEquals(1, defaultAccessControlJson.length());
        assertTrue(defaultAccessControlJson.getJSONObject(0).getBoolean("public"));
        assertEquals("write", defaultAccessControlJson.getJSONObject(0).getString("level"));
    }

    @Test
    public void testPersistentStoreSaveNullDefaultAccessControl() throws Exception {
        PersistentStore persistentStore = new PersistentStore(instrumentationContext);
        persistentStore.defaultAccessControl = null;
        persistentStore.save();

        SharedPreferences pref = instrumentationContext.getSharedPreferences(
                PersistentStore.SKYGEAR_PREF_SPACE,
                Context.MODE_PRIVATE
        );
        assertEquals("[]", pref.getString(PersistentStore.DEFAULT_ACCESS_CONTROL_KEY, "[]"));
    }

    @Test
    public void testPersistentStoreRestoreDeviceId() throws Exception {
        SharedPreferences pref = instrumentationContext.getSharedPreferences(
                PersistentStore.SKYGEAR_PREF_SPACE,
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PersistentStore.DEVICE_ID_KEY, "testing-device-id");
        editor.commit();

        PersistentStore persistentStore = new PersistentStore(instrumentationContext);
        assertEquals("testing-device-id", persistentStore.deviceId);
    }

    @Test
    public void testPersistentStoreRestoreDeviceIdFromEmptyState() throws Exception {
        PersistentStore persistentStore = new PersistentStore(instrumentationContext);
        assertNull(persistentStore.deviceId);
    }

    @Test
    public void testPersistentStoreSaveDeviceId() throws Exception {
        PersistentStore persistentStore = new PersistentStore(instrumentationContext);
        persistentStore.deviceId = "testing-device-id";
        persistentStore.save();

        SharedPreferences pref = instrumentationContext.getSharedPreferences(
                PersistentStore.SKYGEAR_PREF_SPACE,
                Context.MODE_PRIVATE
        );
        assertEquals("testing-device-id", pref.getString(PersistentStore.DEVICE_ID_KEY, null));
    }

    @Test
    public void testPersistentStoreSaveNullDeviceId() throws Exception {
        SharedPreferences pref = instrumentationContext.getSharedPreferences(
                PersistentStore.SKYGEAR_PREF_SPACE,
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PersistentStore.DEVICE_ID_KEY, "testing-device-id");
        editor.commit();

        PersistentStore persistentStore = new PersistentStore(instrumentationContext);
        persistentStore.deviceId = null;
        persistentStore.save();

        assertNull(pref.getString(PersistentStore.DEVICE_ID_KEY, null));
    }

    @Test
    public void testPersistentStoreRestoreDeviceToken() throws Exception {
        SharedPreferences pref = instrumentationContext.getSharedPreferences(
                PersistentStore.SKYGEAR_PREF_SPACE,
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PersistentStore.DEVICE_TOKEN_KEY, "testing-device-token");
        editor.commit();

        PersistentStore persistentStore = new PersistentStore(instrumentationContext);
        assertEquals("testing-device-token", persistentStore.deviceToken);
    }

    @Test
    public void testPersistentStoreRestoreDeviceTokenFromEmptyState() throws Exception {
        PersistentStore persistentStore = new PersistentStore(instrumentationContext);
        assertNull(persistentStore.deviceToken);
    }

    @Test
    public void testPersistentStoreSaveDeviceToken() throws Exception {
        PersistentStore persistentStore = new PersistentStore(instrumentationContext);
        persistentStore.deviceToken = "testing-device-token";
        persistentStore.save();

        SharedPreferences pref = instrumentationContext.getSharedPreferences(
                PersistentStore.SKYGEAR_PREF_SPACE,
                Context.MODE_PRIVATE
        );
        assertEquals("testing-device-token", pref.getString(PersistentStore.DEVICE_TOKEN_KEY, null));
    }

    @Test
    public void testPersistentStoreSaveNullDeviceToken() throws Exception {
        SharedPreferences pref = instrumentationContext.getSharedPreferences(
                PersistentStore.SKYGEAR_PREF_SPACE,
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PersistentStore.DEVICE_TOKEN_KEY, "testing-device-token");
        editor.commit();

        PersistentStore persistentStore = new PersistentStore(instrumentationContext);
        persistentStore.deviceToken = null;
        persistentStore.save();

        assertNull(pref.getString(PersistentStore.DEVICE_TOKEN_KEY, null));
    }
}
