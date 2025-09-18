package com.cmp.service.keys;
import com.cmp.core.crypto.HmacUtil;
import java.nio.charset.StandardCharsets;

/** 统一派生：从 K_src 派生合同级 kLabel 或按关键字派生的 kLabel(w) */
public final class Derive {
    private Derive(){}
    public static byte[] kLabel(byte[] kSrc, String sid, String ds, String clientId){
        return HmacUtil.hmacSha256(kSrc, ("LABEL|"+sid+"|"+ds+"|"+clientId).getBytes(StandardCharsets.UTF_8));
    }
    public static byte[] kLabelPerWord(byte[] kSrc, String sid, String ds, String clientId, String w){
        return HmacUtil.hmacSha256(kSrc, ("LABEL_W|"+sid+"|"+ds+"|"+clientId+"|"+w).getBytes(StandardCharsets.UTF_8));
    }
    public static byte[] alpha(byte[] kSrc, String sid, String ds, String clientId){
        return HmacUtil.hmacSha256(kSrc, ("ALPHA|"+sid+"|"+ds+"|"+clientId).getBytes(StandardCharsets.UTF_8));
    }
}
