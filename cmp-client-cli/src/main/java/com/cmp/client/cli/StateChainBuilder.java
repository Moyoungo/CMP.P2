package com.cmp.client.cli;

import com.cmp.core.crypto.HashUtil;
import com.cmp.core.util.Bytes;
import com.cmp.protocol.api.StateTableApi;
import com.cmp.protocol.dto.StateTableDTO;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/** 仅用于本地演示：从给定 ST0 与 alpha 构造若干个状态并写入云端 S 表 */
public final class StateChainBuilder {
    private StateChainBuilder() {}

    /** 只写入，不返回状态序列 */
    public static void writeChain(StateTableApi api, String sid, String ds, byte[] alpha, byte[] st0, int count){
        byte[] st = st0;
        for (int t = 0; t < count; t++){
            byte[] stNext = new byte[st.length];
            new SecureRandom().nextBytes(stNext);

            byte[] mask = HashUtil.H2(Bytes.concat(
                    sid.getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x00},
                    ds.getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x00},
                    alpha, st
            ));
            byte[] sk = new byte[st.length];
            for (int i = 0; i < st.length; i++) sk[i] = (byte)(stNext[i] ^ mask[i]);

            byte[] h1st = HashUtil.H1(st);
            api.put(new StateTableDTO.PutRequest(sid, ds, t, h1st, sk));

            st = stNext;
        }
        // 断点：最后一个 ST 的条目置空
        byte[] h1Last = HashUtil.H1(st);
        api.put(new StateTableDTO.PutRequest(sid, ds, count, h1Last, null));
    }

    /** 写入并返回包含 ST_0..ST_count 的状态序列（含起点与末尾断点后的最后状态） */
    public static List<byte[]> buildAndWriteChain(StateTableApi api, String sid, String ds, byte[] alpha, byte[] st0, int count){
        List<byte[]> states = new ArrayList<>();
        byte[] st = st0;
        states.add(st0);
        for (int t = 0; t < count; t++){
            byte[] stNext = new byte[st.length];
            new SecureRandom().nextBytes(stNext);

            byte[] mask = HashUtil.H2(Bytes.concat(
                    sid.getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x00},
                    ds.getBytes(StandardCharsets.UTF_8),
                    new byte[]{0x00},
                    alpha, st
            ));
            byte[] sk = new byte[st.length];
            for (int i = 0; i < st.length; i++) sk[i] = (byte)(stNext[i] ^ mask[i]);

            byte[] h1st = HashUtil.H1(st);
            api.put(new StateTableDTO.PutRequest(sid, ds, t, h1st, sk));

            st = stNext;
            states.add(st);
        }
        byte[] h1Last = HashUtil.H1(st);
        api.put(new StateTableDTO.PutRequest(sid, ds, count, h1Last, null)); // 断点
        return states; // 包含 ST_0..ST_count
    }
}
