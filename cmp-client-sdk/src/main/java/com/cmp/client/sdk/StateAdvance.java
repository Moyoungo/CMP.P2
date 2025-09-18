package com.cmp.client.sdk;
import com.cmp.protocol.api.StateTableApi;
import com.cmp.protocol.dto.StateTableDTO;
import com.cmp.core.crypto.HashUtil;
import com.cmp.core.util.Bytes;

/** 客户端前推 ST：ST_{t+1} = SK_t ⊕ H2(alpha||sid||ds||ST_t)；若 SK_t==null 则到达断点。 */
public final class StateAdvance {
    private StateAdvance(){}
    public static byte[] next(StateTableApi api, String sid, String ds, byte[] alpha, byte[] stNow){
        byte[] h1st = HashUtil.H1(stNow);
        StateTableDTO.GetResponse resp = api.get(new StateTableDTO.GetRequest(h1st));
        byte[] sk = resp.sk; // 可能为 null
        if (sk == null) return null; // 断点
        byte[] mask = HashUtil.H2(com.cmp.core.util.Bytes.concat(
            sid.getBytes(java.nio.charset.StandardCharsets.UTF_8),
            new byte[]{0x00},
            ds.getBytes(java.nio.charset.StandardCharsets.UTF_8),
            new byte[]{0x00},
            alpha,
            stNow
        ));
        if (sk.length != mask.length) throw new IllegalStateException("sk/mask length mismatch");
        byte[] nxt = new byte[sk.length];
        for (int i=0;i<sk.length;i++) nxt[i] = (byte)(sk[i] ^ mask[i]);
        return nxt;
    }
}
