package com.cmp.agent.util;
import com.cmp.core.crypto.HashUtil;
import java.util.ArrayList;
import java.util.List;

/** 简易 Merkle 树（单线程），用于批次 H(doc) 承诺。 */
public final class Merkle {
    public static byte[] root(List<byte[]> leaves){
        if (leaves.isEmpty()) return HashUtil.H(new byte[]{0x00});
        List<byte[]> level = new java.util.ArrayList<>(leaves);
        while (level.size() > 1){
            List<byte[]> next = new ArrayList<>((level.size()+1)/2);
            for (int i=0;i<level.size();i+=2){
                byte[] a = level.get(i);
                byte[] b = (i+1<level.size()? level.get(i+1) : a);
                next.add(HashUtil.H(a,b));
            }
            level = next;
        }
        return level.get(0);
    }
}
