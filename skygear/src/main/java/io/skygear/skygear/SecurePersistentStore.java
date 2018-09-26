package io.skygear.skygear;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

/**
 * The Skygear secure persistent store.
 *
 * This class manages persistent data of Skygear, it will encrypt current user data and access token
 * before storing to SharedPreferences
 */
class SecurePersistentStore extends PersistentStore {
    private static final String TAG = "Skygear SDK";

    static final String KEY_ALIAS = "SkygearKey";

    static final String CURRENT_USER_ENCRYPTED_KEY = "current_user_encrypted";
    static final String ACCESS_TOKEN_ENCRYPTED_KEY = "access_token_encrypted";
    static final String CURRENT_USER_ENCRYPTED_KEY_KEY = "current_user_encrypted_key";
    static final String ACCESS_TOKEN_ENCRYPTED_KEY_KEY = "access_token_encrypted_key";

    private static final String RSA_MODE =  "RSA/ECB/PKCS1Padding";
    private static final String AES_MODE = "AES/ECB/PKCS5Padding";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    Encryptor encryptor;
    Decryptor decryptor;

    public SecurePersistentStore(Context context) {
        super(context);
    }

    @Override
    void restoreAuthUser(SharedPreferences pref) {
        String encryptedCurrentUser = pref.getString(CURRENT_USER_ENCRYPTED_KEY, null);
        String encryptedKey = pref.getString(CURRENT_USER_ENCRYPTED_KEY_KEY, null);

        if (encryptedCurrentUser == null || encryptedKey == null) {
            this.currentUser = null;
            return;
        }

        try {
            String currentUserString = getDecryptor().decryptData(encryptedKey, encryptedCurrentUser);
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
                EncryptedResult r = getEncryptor().encryptText(currentUserString);
                prefEditor.putString(CURRENT_USER_ENCRYPTED_KEY, r.encryptedData);
                prefEditor.putString(CURRENT_USER_ENCRYPTED_KEY_KEY, r.encryptedKey);
            } catch (Exception e) {
                Log.w(TAG, "Fail to encrypt and save current user object", e);
            }
        } else {
            prefEditor.remove(CURRENT_USER_ENCRYPTED_KEY);
            prefEditor.remove(CURRENT_USER_ENCRYPTED_KEY_KEY);
        }
    }

    @Override
    void restoreAccessToken(SharedPreferences pref) {
        String encryptedAccessToken = pref.getString(ACCESS_TOKEN_ENCRYPTED_KEY, null);
        String encryptedKey = pref.getString(ACCESS_TOKEN_ENCRYPTED_KEY_KEY, null);

        if (encryptedAccessToken == null || encryptedKey == null) {
            this.accessToken = null;
            return;
        }

        try {
            this.accessToken = getDecryptor().decryptData(encryptedKey, encryptedAccessToken);
        } catch (Exception e) {
            Log.w(TAG, "Fail to decrypt saved access token", e);
            this.accessToken = null;
        }
    }

    @Override
    void saveAccessToken(SharedPreferences.Editor prefEditor) {
        if (this.accessToken != null) {
            try {
                EncryptedResult r = getEncryptor().encryptText(this.accessToken);
                prefEditor.putString(ACCESS_TOKEN_ENCRYPTED_KEY, r.encryptedData);
                prefEditor.putString(ACCESS_TOKEN_ENCRYPTED_KEY_KEY, r.encryptedKey);
            } catch (Exception e) {
                Log.w(TAG, "Fail to encrypt and save access token", e);
            }
        } else {
            prefEditor.remove(ACCESS_TOKEN_ENCRYPTED_KEY);
            prefEditor.remove(ACCESS_TOKEN_ENCRYPTED_KEY_KEY);
        }
    }

    private Encryptor getEncryptor() {
        if (encryptor == null) {
            encryptor = new Encryptor();
        }
        return encryptor;
    }

    private Decryptor getDecryptor() {
        if (decryptor == null) {
            decryptor = new Decryptor();
        }
        return decryptor;
    }

    class Decryptor {

        private KeyStore keyStore;

        private byte[] rsaDecrypt(byte[] encrypted) throws KeyStoreException,
                CertificateException, NoSuchAlgorithmException, IOException, NoSuchProviderException,
                NoSuchPaddingException, InvalidKeyException, UnrecoverableEntryException {

            if (keyStore == null) {
                keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
                keyStore.load(null);
            }
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(
                    KEY_ALIAS, null);
            Cipher output = Cipher.getInstance(RSA_MODE);
            output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());
            CipherInputStream cipherInputStream = new CipherInputStream(
                    new ByteArrayInputStream(encrypted), output);
            ArrayList<Byte> values = new ArrayList<>();
            int nextByte;
            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte)nextByte);
            }

            byte[] bytes = new byte[values.size()];
            for(int i = 0; i < bytes.length; i++) {
                bytes[i] = values.get(i).byteValue();
            }
            return bytes;
        }

        String decryptData(final String encryptedKey, final String encryptedData) throws NoSuchPaddingException,
                NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException,
                InvalidKeyException, IOException, CertificateException, NoSuchProviderException,
                BadPaddingException, IllegalBlockSizeException {
            byte[] encryptedKeyBytes = Base64.decode(encryptedKey, Base64.DEFAULT);
            byte[] encryptedDataBytes = Base64.decode(encryptedData, Base64.DEFAULT);
            byte[] secretKey = rsaDecrypt(encryptedKeyBytes);

            Cipher c = Cipher.getInstance(AES_MODE);
            c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secretKey, "AES"));
            byte[] decodedBytes = c.doFinal(encryptedDataBytes);
            return new String(decodedBytes, "UTF-8");
        }
    }

    class Encryptor {

        private KeyStore keyStore;

        Encryptor() {
            super();
        }

        EncryptedResult encryptText(final String textToEncrypt) throws NoSuchPaddingException,
                NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IOException,
                BadPaddingException, IllegalBlockSizeException, CertificateException, KeyStoreException,
                UnrecoverableEntryException, InvalidAlgorithmParameterException {
            byte[] secretKey = generateAESSecret();
            byte[] encryptedKeyBytes = rsaEncrypt(secretKey);
            String encryptedKey = Base64.encodeToString(encryptedKeyBytes, Base64.DEFAULT);

            Cipher c = Cipher.getInstance(AES_MODE);
            c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secretKey, "AES"));
            byte[] encodedBytes = c.doFinal(textToEncrypt.getBytes("UTF-8"));
            String encryptedBase64Encoded = Base64.encodeToString(encodedBytes, Base64.DEFAULT);
            return new EncryptedResult(encryptedKey, encryptedBase64Encoded);
        }

        private byte[] generateAESSecret() {
            byte[] key = new byte[16];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(key);
            return key;
        }

        private byte[] rsaEncrypt(byte[] secret) throws NoSuchAlgorithmException,
                IOException, NoSuchProviderException, NoSuchPaddingException,
                InvalidKeyException, InvalidAlgorithmParameterException, CertificateException,
                KeyStoreException, UnrecoverableEntryException {
            PublicKey publicKey = getRSAPublicKey();
            Cipher inputCipher = Cipher.getInstance(RSA_MODE);
            inputCipher.init(Cipher.ENCRYPT_MODE, publicKey);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, inputCipher);
            cipherOutputStream.write(secret);
            cipherOutputStream.close();

            byte[] vals = outputStream.toByteArray();
            return vals;
        }

        private PublicKey getRSAPublicKey() throws NoSuchAlgorithmException,
                NoSuchProviderException, InvalidAlgorithmParameterException, KeyStoreException,
                IOException, CertificateException, UnrecoverableEntryException {
            if (keyStore == null) {
                keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
                keyStore.load(null);
            }

            if (!keyStore.containsAlias(KEY_ALIAS)) {
                return generateRSAPublicKey(KEY_ALIAS);
            }

            return ((KeyStore.PrivateKeyEntry) keyStore.getEntry(KEY_ALIAS, null))
                    .getCertificate().getPublicKey();
        }

        private PublicKey generateRSAPublicKey(final String alias) throws NoSuchAlgorithmException,
                NoSuchProviderException, InvalidAlgorithmParameterException {
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.add(Calendar.YEAR, 100);
            KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                    .setAlias(alias)
                    .setSerialNumber(BigInteger.ONE)
                    .setSubject(new X500Principal("CN=" + alias))
                    .setStartDate(start.getTime())
                    .setEndDate(end.getTime())
                    .build();
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", ANDROID_KEY_STORE);
            generator.initialize(spec);

            KeyPair keyPair = generator.generateKeyPair();
            return keyPair.getPublic();
        }
    }

    static class EncryptedResult {
        String encryptedKey;
        String encryptedData;

        EncryptedResult(String encryptedKey, String encryptedData) {
            super();
            this.encryptedKey = encryptedKey;
            this.encryptedData = encryptedData;
        }
    }
}
