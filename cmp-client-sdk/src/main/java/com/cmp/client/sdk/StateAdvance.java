package com.cmp.client.sdk;
import com.cmp.protocol.api.StateTableApi;
import com.cmp.protocol.dto.StateTableDTO;
import com.cmp.core.crypto.HashUtil;
import com.cmp.core.util.Bytes;
import java.nio.charset.StandardCharsets;

/** 客户端前推 ST：从 S 表取出 SK_t，计算 ST_{t+1} = SK_t ⊕ H2(sid||0x00||ds||0x00||alpha||ST_t)。 */
public final class StateAdvance {
    private StateAdvance(){}

    /** 若返回 null 表示到达链尾（断点）。兼容 sk==null 与 sk.length==0 两种哨兵。 */
    public static byte[] next(StateTableApi api, String sid, String ds, byte[] alpha, byte[] stNow){
        byte[] h1st = HashUtil.H1(stNow);
        StateTableDTO.GetResponse resp = api.get(new StateTableDTO.GetRequest(h1st));
        byte[] sk = resp == null ? null : resp.sk; // 可能为 null 或空数组
        if (sk == null || sk.length == 0){
            return null; // 链尾
        }
        byte[] mask = HashUtil.H2(Bytes.concat(
                sid.getBytes(StandardCharsets.UTF_8),
                new byte[]{0x00},
                ds.getBytes(StandardCharsets.UTF_8),
                new byte[]{0x00},
                alpha, stNow
        ));
        if (mask.length != sk.length){
            // 为了鲁棒性，取较短长度进行 XOR；真实系统可切换为抛异常
            int n = Math.min(mask.length, sk.length);
            byte[] out = new byte[n];
            for (int i = 0; i < n; i++) out[i] = (byte)(sk[i] ^ mask[i]);
            return out;
        }
        byte[] nxt = new byte[sk.length];
        for (int i=0;i<sk.length;i++) nxt[i] = (byte)(sk[i] ^ mask[i]);
        return nxt;
    }
}
