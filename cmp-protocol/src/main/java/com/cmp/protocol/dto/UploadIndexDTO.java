package com.cmp.protocol.dto;
import com.cmp.core.model.PostingEntry;
import java.util.List;

public final class UploadIndexDTO {
    public static final class CiphertextsRequest {
        public final String sid; public final String ds; public final int t;
        public final List<byte[]> ciphertextBlobs;
        public CiphertextsRequest(String sid, String ds, int t, List<byte[]> ciphertextBlobs){
            this.sid=sid; this.ds=ds; this.t=t; this.ciphertextBlobs=ciphertextBlobs;
        }
    }
    public static final class IndexDeltaRequest {
        public final List<PostingEntry> entries;
        public IndexDeltaRequest(List<PostingEntry> entries){ this.entries = entries; }
    }
}
