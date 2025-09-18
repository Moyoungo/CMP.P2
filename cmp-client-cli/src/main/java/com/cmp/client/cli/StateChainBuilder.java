package com.cmp.client.cli;
import com.cmp.core.crypto.HashUtil;
import com.cmp.core.util.Bytes;
import com.cmp.protocol.api.StateTableApi;
import com.cmp.protocol.dto.StateTableDTO;

/** 仅用于本地演示：从给定 ST0 与 alpha 构造若干个状态并写入云端 S 表 */
public final class StateChainBuilder {
    public static void writeChain(StateTableApi api, String sid, String ds, byte[] alpha, byte[] st0, int count){
        byte[] st = st0;
        for (int t=0; t<count; t++){
            byte[] stNext = new byte[st.length];
            new java.security.SecureRandom().nextBytes(stNext);
            byte[] mask = HashUtil.H2(Bytes.concat(
                sid.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                new byte[]{0x00},
                ds.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                new byte[]{0x00},
                alpha, st
            ));
            byte[] sk = new byte[st.length];
            for (int i=0;i<st.length;i++) sk[i] = (byte)(stNext[i] ^ mask[i]);
            byte[] h1st = HashUtil.H1(st);
            api.put(new StateTableDTO.PutRequest(sid, ds, t, h1st, sk));
            st = stNext;
        }
        // 断点：最后一个 ST 的条目置空
        byte[] h1Last = HashUtil.H1(st);
        api.put(new StateTableDTO.PutRequest(sid, ds, count, h1Last, null));
    }
}
