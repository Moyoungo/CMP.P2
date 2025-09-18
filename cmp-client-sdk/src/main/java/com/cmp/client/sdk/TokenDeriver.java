package com.cmp.client.sdk;
import com.cmp.core.crypto.HmacUtil;
import java.nio.charset.StandardCharsets;

/** 生成 tokenW */
public final class TokenDeriver {
    private TokenDeriver(){}
    public static byte[] tokenFor(GrantSession sess, String keyword){
        byte[] key;
        if (sess.mode == com.cmp.core.model.Grant.Mode.ALL_KEYWORDS){
            key = sess.kLabel;
        } else {
            key = sess.perKeywordKLabels.get(keyword);
            if (key == null) throw new IllegalArgumentException("keyword not granted: "+keyword);
        }
        return HmacUtil.hmacSha256(key, keyword.getBytes(StandardCharsets.UTF_8));
    }
}
