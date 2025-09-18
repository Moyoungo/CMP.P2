package com.cmp.core.crypto;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** 哈希工具：H, H1, H2（域分离），基于 SHA-256 */
public final class HashUtil {
    private HashUtil() {}
    private static final String ALG = "SHA-256";

    public static byte[] H(byte[]... parts) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALG);
            for (byte[] p : parts) if (p != null) md.update(p);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    /** H1: 带前缀域分离 */
    public static byte[] H1(byte[] data) {
        return H(new byte[]{0x01}, data);
    }
    /** H2: 带前缀域分离 */
    public static byte[] H2(byte[] data) {
        return H(new byte[]{0x02}, data);
    }

    /** H(sid|ds|HMAC(klabel,w)|H1(ST)) 的便捷方法 */
    public static byte[] labelHash(byte[] sid, byte[] ds, byte[] tokenW, byte[] h1st) {
        return H(new byte[]{0x11}, sid, ds, tokenW, h1st);
    }
}
