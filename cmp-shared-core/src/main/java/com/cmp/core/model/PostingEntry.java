package com.cmp.core.model;
import java.util.List;
import java.util.Objects;

/** 倒排表条目：label -> docIds */
public final class PostingEntry {
    public final byte[] label;
    public final List<Integer> docIds;
    public final String sid;
    public final String ds;
    public final int t;
    public PostingEntry(String sid, String ds, int t, byte[] label, List<Integer> docIds) {
        this.sid = Objects.requireNonNull(sid);
        this.ds = Objects.requireNonNull(ds);
        this.t = t;
        this.label = Objects.requireNonNull(label);
        this.docIds = Objects.requireNonNull(docIds);
    }
}
