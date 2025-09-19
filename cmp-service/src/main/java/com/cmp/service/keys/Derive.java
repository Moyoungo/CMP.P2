package com.cmp.service.keys;

import com.cmp.core.keys.Kdf;
import java.nio.charset.StandardCharsets;

/** 服务端派生的薄封装：复用 shared-core 的 Kdf */
public final class Derive {
    private Derive(){}

    /** 返回“数据集级标签密钥”给 ALL_KEYWORDS 模式 */
    public static byte[] kLabel(byte[] kSrc, String sid, String ds, String clientIdIgnored){
        return Kdf.kLabelDS(kSrc, sid, ds); // clientId 不参与
    }

    /** PER_KEYWORD 模式下，直接返回每个关键字的 tokenW */
    public static byte[] kLabelPerWord(byte[] kSrc, String sid, String ds, String clientIdIgnored, String w){
        byte[] kLabelDS = Kdf.kLabelDS(kSrc, sid, ds);
        return Kdf.tokenW(kLabelDS, w); // 注意：这是 tokenW，不是“key”
    }

    /** α 仍可按合同个性化（用于状态链掩码） */
    public static byte[] alpha(byte[] kSrc, String sid, String ds, String clientId){
        String label = "ALPHA|" + sid + "|" + ds + "|" + clientId;
        return com.cmp.core.crypto.HmacUtil.hmacSha256(kSrc, label.getBytes(StandardCharsets.UTF_8));
    }
}
