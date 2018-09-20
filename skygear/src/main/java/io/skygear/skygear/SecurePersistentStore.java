package io.skygear.skygear;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/**
 * The Skygear secure persistent store.
 *
 * This class manages persistent data of Skygear, it will encrypt current user data and access token
 * before storing to SharedPreferences
 */
class SecurePersistentStore extends PersistentStore {
    private static final String TAG = "Skygear SDK";

    static final String ACCESS_TOKEN_KEY_ALIAS = "SkygearAccessTokenKey";
    static final String CURRENT_USER_KEY_ALIAS = "SkygearCurrentUserKey";

    static final String CURRENT_USER_ENCRYPTED_KEY = "current_user_encrypted";
    static final String ACCESS_TOKEN_ENCRYPTED_KEY = "access_token_encrypted";
    static final String CURRENT_USER_IV_KEY = "current_user_iv";
    static final String ACCESS_TOKEN_IV_KEY = "access_token_iv";

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    Encryptor encryptor;
    Decryptor decryptor;

    public SecurePersistentStore(Context context) {
        super(context);
    }

    @Override
    void restoreAuthUser(SharedPreferences pref) {
        String encryptedCurrentUser = pref.getString(CURRENT_USER_ENCRYPTED_KEY, null);
        String encodedIV = pref.getString(CURRENT_USER_IV_KEY, null);

        if (encryptedCurrentUser == null || encodedIV == null) {
            this.currentUser = null;
            return;
        }

        try {
            byte[] encryptedData = base64DecodeToByte(encryptedCurrentUser);
            byte[] iv = base64DecodeToByte(encodedIV);
            String currentUserString = getDecryptor().decryptData(CURRENT_USER_KEY_ALIAS, encryptedData, iv);
            this.currentUser = RecordSerializer.deserialize(
                    new JSONObject(currentUserString)
            );
        } catch (Exception e) {
            Log.w(TAG, "Fail to decrypt saved current user object", e);
            this.currentUser = null;
        }
    }

    @Override
    void saveAuthUser(SharedPreferences.Editor prefEditor) {
        if (this.currentUser != null) {
            String currentUserString = RecordSerializer.serialize(this.currentUser).toString();
            try {
                EncryptedResult r = getEncryptor().encryptText(CURRENT_USER_KEY_ALIAS, currentUserString);
                String encryptedCurrentUser = base64EncodeToString(r.encryptedData);
                String encodedIV = base64EncodeToString(r.initializationVector);
                prefEditor.putString(CURRENT_USER_ENCRYPTED_KEY, encryptedCurrentUser);
                prefEditor.putString(CURRENT_USER_IV_KEY, encodedIV);
            } catch (Exception e) {
                Log.w(TAG, "Fail to encrypt and save current user object", e);
            }
        } else {
            prefEditor.remove(CURRENT_USER_ENCRYPTED_KEY);
            prefEditor.remove(CURRENT_USER_IV_KEY);
        }
    }

    @Override
    void restoreAccessToken(SharedPreferences pref) {
        String encryptedAccessToken = pref.getString(ACCESS_TOKEN_ENCRYPTED_KEY, null);
        String encodedIV = pref.getString(ACCESS_TOKEN_IV_KEY, null);

        if (encryptedAccessToken == null || encodedIV == null) {
            this.accessToken = null;
            return;
        }

        try {
            byte[] encryptedData = base64DecodeToByte(encryptedAccessToken);
            byte[] iv = base64DecodeToByte(encodedIV);
            this.accessToken = getDecryptor().decryptData(ACCESS_TOKEN_KEY_ALIAS, encryptedData, iv);
        } catch (Exception e) {
            Log.w(TAG, "Fail to decrypt saved access token", e);
            this.accessToken = null;
        }
    }

    @Override
    void saveAccessToken(SharedPreferences.Editor prefEditor) {
        if (this.accessToken != null) {
            try {
                EncryptedResult r = getEncryptor().encryptText(ACCESS_TOKEN_KEY_ALIAS, this.accessToken);
                String ecryptedAccessToken = base64EncodeToString(r.encryptedData);
                String encodedIV = base64EncodeToString(r.initializationVector);
                prefEditor.putString(ACCESS_TOKEN_ENCRYPTED_KEY, ecryptedAccessToken);
                prefEditor.putString(ACCESS_TOKEN_IV_KEY, encodedIV);
            } catch (Exception e) {
                Log.w(TAG, "Fail to encrypt and save access token", e);
            }
        } else {
            prefEditor.remove(ACCESS_TOKEN_ENCRYPTED_KEY);
            prefEditor.remove(ACCESS_TOKEN_IV_KEY);
        }
    }

    protected String base64EncodeToString(byte[] b) {
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    protected byte[] base64DecodeToByte(String s) {
        return Base64.decode(s, Base64.DEFAULT);
    }

    protected Encryptor getEncryptor() {
        if (encryptor == null) {
            encryptor = new Encryptor();
        }
        return encryptor;
    }

    protected Decryptor getDecryptor() {
        if (decryptor == null) {
            decryptor = new Decryptor();
        }
        return decryptor;
    }

    class Decryptor {

        private KeyStore keyStore;

        String decryptData(final String alias, final byte[] encrypted, final byte[] iv) throws NoSuchPaddingException,
                NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException, InvalidAlgorithmParameterException,
                InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException, CertificateException {
            final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            final GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(alias), spec);
            return new String(cipher.doFinal(encrypted), "UTF-8");
        }

        private SecretKey getSecretKey(final String alias) throws UnrecoverableEntryException,
                NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException {
            if (keyStore == null) {
                keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
                keyStore.load(null);
            }
            return ((KeyStore.SecretKeyEntry) keyStore.getEntry(alias, null)).getSecretKey();
        }
    }

    class Encryptor {

        EncryptedResult encryptText(final String alias, final String textToEncrypt) throws NoSuchPaddingException,
                NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException,
                InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {

            final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(alias));
            byte[] iv = cipher.getIV();

            return new EncryptedResult(cipher.doFinal(textToEncrypt.getBytes("UTF-8")), iv);
        }

        private SecretKey getSecretKey(final String alias) throws NoSuchAlgorithmException,
                NoSuchProviderException, InvalidAlgorithmParameterException {
            KeyGenerator keyGenerator;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
                keyGenerator.init(new KeyGenParameterSpec.Builder(
                        alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .build());
            } else {
                keyGenerator = KeyGenerator.getInstance(ANDROID_KEY_STORE);
                // fixup init keyGenerator
            }
            return keyGenerator.generateKey();
        }

    }

    class EncryptedResult {
        byte[] encryptedData;
        byte[] initializationVector;

        EncryptedResult(byte[] data, byte[] iv) {
            encryptedData = data;
            initializationVector = iv;
        }
    }
}
