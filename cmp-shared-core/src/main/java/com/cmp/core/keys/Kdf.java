package com.cmp.core.keys;

import com.cmp.core.crypto.HmacUtil;
import java.nio.charset.StandardCharsets;

/** 统一密钥派生：数据源/客户端/CMP 均可复用 */
public final class Kdf {
    private Kdf(){}

    /** 数据加密用子密钥（避免直接把 K_src 当 AES 密钥用） */
    public static byte[] kEnc(byte[] kSrc){
        return HmacUtil.hmacSha256(kSrc, "ENC".getBytes(StandardCharsets.UTF_8));
    }

    /** 数据集级标签密钥：用于构造 tokenW 与服务端标签 */
    public static byte[] kLabelDS(byte[] kSrc, String sid, String ds){
        return HmacUtil.hmacSha256(kSrc, ("LABEL_DS|"+sid+"|"+ds).getBytes(StandardCharsets.UTF_8));
    }

    /** tokenW = HMAC(K_LABEL_DS, w) */
    public static byte[] tokenW(byte[] kLabelDS, String w){
        return HmacUtil.hmacSha256(kLabelDS, w.getBytes(StandardCharsets.UTF_8));
    }
}
