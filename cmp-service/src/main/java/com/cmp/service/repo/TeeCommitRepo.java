package com.cmp.service.repo;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/** 存储每批次的 Merkle 根与证明（占位），用于审计 */
public final class TeeCommitRepo {
    public static final class Entry {
        public final String sid, ds; public final int t; public final byte[] merkleRoot; public final byte[] attestation;
        public Entry(String sid, String ds, int t, byte[] merkleRoot, byte[] attestation){
            this.sid=sid; this.ds=ds; this.t=t; this.merkleRoot=merkleRoot; this.attestation=attestation;
        }
    }
    private final Map<String, Entry> map = new ConcurrentHashMap<>();
    private static String key(String sid, String ds, int t){ return sid+"|"+ds+"|"+t; }
    public void put(Entry e){ map.put(key(e.sid,e.ds,e.t), e); }
}
