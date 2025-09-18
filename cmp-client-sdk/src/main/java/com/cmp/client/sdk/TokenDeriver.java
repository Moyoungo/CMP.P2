package com.cmp.client.sdk;

import com.cmp.core.model.Grant;
import com.cmp.core.keys.Kdf;
import java.nio.charset.StandardCharsets;

/** 生成 tokenW：ALL 用 K_LABEL_DS 算；PER 直接使用服务端给的 tokenW */
public final class TokenDeriver {
    private TokenDeriver(){}
    public static byte[] tokenFor(GrantSession sess, String keyword){
        if (sess.mode == Grant.Mode.ALL_KEYWORDS){
            return Kdf.tokenW(sess.kLabel, keyword); // kLabel = K_LABEL_DS
        } else {
            byte[] token = sess.perKeywordKLabels.get(keyword); // 这里存的是 tokenW
            if (token == null) throw new IllegalArgumentException("keyword not granted: "+keyword);
            return token;
        }
    }
}
