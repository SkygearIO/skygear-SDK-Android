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
                        "\"_id\": \"123\"," +
                        "\"access_token\": \"token_123\"," +
                        "\"username\": \"user_123\"," +
                        "\"email\": \"user123@skygear.dev\"" +

                "}"
        );
        editor.commit();

        PersistentStore persistentStore = new PersistentStore(instrumentationContext);
        User currentUser = persistentStore.currentUser;

        assertEquals("123", currentUser.id);
        assertEquals("token_123", currentUser.accessToken);
        assertEquals("user_123", currentUser.username);
        assertEquals("user123@skygear.dev", currentUser.email);
    }

    @Test
    public void testPersistentStoreRestoreUserFromEmptyState() throws Exception {
        PersistentStore persistentStore = new PersistentStore(instrumentationContext);
        assertNull(persistentStore.currentUser);
    }

    @Test
    public void testPersistentStoreSaveUser() throws Exception {
        PersistentStore persistentStore = new PersistentStore(instrumentationContext);
        persistentStore.currentUser = new User(
                "12345",
                "token_12345",
                "user_12345",
                "user12345@skygear.dev"
        );
        persistentStore.save();

        SharedPreferences pref = instrumentationContext.getSharedPreferences(
                PersistentStore.SKYGEAR_PREF_SPACE,
                Context.MODE_PRIVATE
        );

        String currentUserString = pref.getString(PersistentStore.CURRENT_USER_KEY, "{}");
        JSONObject currentUserJson = new JSONObject(currentUserString);

        assertEquals("12345", currentUserJson.getString("_id"));
        assertEquals("token_12345", currentUserJson.getString("access_token"));
        assertEquals("user_12345", currentUserJson.getString("username"));
        assertEquals("user12345@skygear.dev", currentUserJson.getString("email"));
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
}
