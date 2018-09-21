package io.skygear.skygear;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

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
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
@SuppressLint("CommitPrefEdits")
public class SecurePersistentStoreUnitTest {
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
    public void testPersistentStoreRestoreUserFromEmptyState() throws Exception {
        SecurePersistentStore persistentStore = new SecurePersistentStore(instrumentationContext);
        assertNull(persistentStore.currentUser);
    }

    @Test
    public void testPersistentStoreSaveAndRestoreUser() throws Exception {
        SecurePersistentStore persistentStore = new SecurePersistentStore(instrumentationContext);

        // Save user
        Map profile = new HashMap<>();
        profile.put("username", "user_12345");
        profile.put("email", "user12345@skygear.dev");

        persistentStore.currentUser = new Record("user", "12345", profile);
        persistentStore.accessToken = "token_12345";
        persistentStore.save();

        // Restore current user
        SecurePersistentStore newPersistentStore = new SecurePersistentStore(instrumentationContext);
        Record currentUser = newPersistentStore.currentUser;

        assertEquals("user", currentUser.type);
        assertEquals("12345", currentUser.id);
        assertEquals("user_12345", currentUser.get("username"));
        assertEquals("user12345@skygear.dev", currentUser.get("email"));

        assertEquals("token_12345", persistentStore.accessToken);

        // Current user information should be store in encrypted fields
        SharedPreferences pref = instrumentationContext.getSharedPreferences(
                SecurePersistentStore.SKYGEAR_PREF_SPACE,
                Context.MODE_PRIVATE
        );

        String currentUserString = pref.getString(PersistentStore.CURRENT_USER_KEY, null);
        String currentUserEncryptedString = pref.getString(
                SecurePersistentStore.CURRENT_USER_ENCRYPTED_KEY, null);
        String currentUserEncryptedKey = pref.getString(
                SecurePersistentStore.CURRENT_USER_ENCRYPTED_KEY_KEY, null);
        assertNull(currentUserString);
        assertNotNull(currentUserEncryptedString);
        assertNotNull(currentUserEncryptedKey);

        String accessToken = pref.getString(PersistentStore.ACCESS_TOKEN_KEY, null);
        String accessTokenEncrypted = pref.getString
                (SecurePersistentStore.ACCESS_TOKEN_ENCRYPTED_KEY, null);
        String accessTokenEncryptedKey = pref.getString(
                SecurePersistentStore.ACCESS_TOKEN_ENCRYPTED_KEY_KEY, null);
        assertNull(accessToken);
        assertNotNull(accessTokenEncrypted);
        assertNotNull(accessTokenEncryptedKey);
    }
}
