package com.cmp.protocol.dto;

public final class StateTableDTO {
    public static final class PutRequest {
        public final String sid; public final String ds; public final int t;
        public final byte[] h1st; public final byte[] sk; // sk==null 表示断点 ⊥
        public PutRequest(String sid, String ds, int t, byte[] h1st, byte[] sk){
            this.sid=sid; this.ds=ds; this.t=t; this.h1st=h1st; this.sk=sk;
        }
    }
    public static final class GetRequest {
        public final byte[] h1st;
        public GetRequest(byte[] h1st){ this.h1st = h1st; }
    }
    public static final class GetResponse {
        public final byte[] sk; // 可能为 null（断点）
        public GetResponse(byte[] sk){ this.sk = sk; }
    }
}
