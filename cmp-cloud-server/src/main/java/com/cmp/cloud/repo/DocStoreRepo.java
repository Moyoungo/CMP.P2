package com.cmp.cloud.repo;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** 内存版密文存储：按 (sid:ds:t) 收集 blobs，仅用于一致性抽样校验 */
public final class DocStoreRepo {
    private final Map<String, List<byte[]>> store = new ConcurrentHashMap<>();
    private static String key(String sid, String ds, int t){ return sid+":"+ds+":"+t; }
    public void put(String sid, String ds, int t, List<byte[]> blobs){
        store.computeIfAbsent(key(sid,ds,t), k-> new java.util.ArrayList<>()).addAll(blobs);
    }
    public List<byte[]> get(String sid, String ds, int t){
        return store.getOrDefault(key(sid,ds,t), java.util.List.of());
    }
}
