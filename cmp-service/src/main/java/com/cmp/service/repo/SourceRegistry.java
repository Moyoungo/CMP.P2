package com.cmp.service.repo;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/** 数据源注册表：sid -> K_src（bytes） */
public final class SourceRegistry {
    private final Map<String, byte[]> map = new ConcurrentHashMap<>();
    public void put(String sid, byte[] ksrc){ map.put(sid, ksrc); }
    public byte[] get(String sid){ return map.get(sid); }
    public boolean exists(String sid){ return map.containsKey(sid); }
}
