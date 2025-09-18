package com.cmp.protocol.dto;
import java.util.List;

public final class SearchDTO {
    public static final class Request {
        public final String sid; public final String ds;
        public final byte[] tokenW; // HMAC(K_label,w) 或每关键字独立 K_label(w) 派生的 token
        public final byte[] stNow;  // 当前使用的 ST_t
        public Request(String sid, String ds, byte[] tokenW, byte[] stNow){
            this.sid=sid; this.ds=ds; this.tokenW=tokenW; this.stNow=stNow;
        }
    }
    public static final class Response {
        public final List<Integer> docIds;
        public final boolean hasMore;
        public Response(List<Integer> docIds, boolean hasMore){
            this.docIds=docIds; this.hasMore=hasMore;
        }
    }
}
