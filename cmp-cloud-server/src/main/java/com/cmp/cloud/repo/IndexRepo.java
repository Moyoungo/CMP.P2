package com.cmp.cloud.repo;
import com.cmp.cloud.util.ByteArrayWrapper;
import com.cmp.core.model.PostingEntry;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** 内存版倒排索引仓库：label -> docIds（可按 (sid,ds,t) 分片拓展） */
public final class IndexRepo {
    private final Map<ByteArrayWrapper, List<Integer>> postings = new ConcurrentHashMap<>();
    public void applyDelta(List<PostingEntry> entries){
        for (PostingEntry e : entries){
            ByteArrayWrapper k = new ByteArrayWrapper(e.label);
            postings.compute(k, (key, old) -> {
                if (old == null) old = new java.util.ArrayList<>();
                old.addAll(e.docIds);
                return old;
            });
        }
    }
    public List<Integer> get(byte[] label){
        return postings.getOrDefault(new ByteArrayWrapper(label), java.util.List.of());
    }
}
