package com.cmp.core.crypto;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/** AES-GCM 加解密（支持 AAD） */
public final class AesGcm {
    private AesGcm(){}
    public static final int NONCE_LEN = 12;
    public static final int TAG_LEN_BITS = 128;

    public static byte[] encrypt(byte[] key, byte[] nonce, byte[] aad, byte[] plaintext) {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LEN_BITS, nonce);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), spec);
            if (aad != null) cipher.updateAAD(aad);
            return cipher.doFinal(plaintext);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    public static byte[] decrypt(byte[] key, byte[] nonce, byte[] aad, byte[] ciphertext) {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LEN_BITS, nonce);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), spec);
            if (aad != null) cipher.updateAAD(aad);
            return cipher.doFinal(ciphertext);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    public static byte[] randomKey() {
        try {
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(256);
            SecretKey k = kg.generateKey();
            return k.getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    public static byte[] randomNonce() {
        byte[] n = new byte[NONCE_LEN];
        new SecureRandom().nextBytes(n);
        return n;
    }
}
