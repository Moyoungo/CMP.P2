package com.cmp.cloud.repo;
import com.cmp.cloud.util.ByteArrayWrapper;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** 内存版状态表 S：H1(ST_t) -> SK_t（sk==null 表示断点） */
public final class StateTableRepo {
    private final Map<ByteArrayWrapper, byte[]> map = new ConcurrentHashMap<>();
    public void put(byte[] h1st, byte[] sk){ map.put(new ByteArrayWrapper(h1st), sk); }
    public byte[] get(byte[] h1st){ return map.get(new ByteArrayWrapper(h1st)); }
}
