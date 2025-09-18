package com.cmp.core.crypto;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

/** HMAC-SHA256 工具 */
public final class HmacUtil {
    private HmacUtil(){}
    public static byte[] hmacSha256(byte[] key, byte[] data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            return mac.doFinal(data);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }
    public static byte[] hmacSha256(byte[] key, String dataUtf8) {
        return hmacSha256(key, dataUtf8.getBytes(StandardCharsets.UTF_8));
    }
}
