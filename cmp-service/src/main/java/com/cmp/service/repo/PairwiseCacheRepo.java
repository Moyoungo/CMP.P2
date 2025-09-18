package com.cmp.service.repo;
import com.cmp.core.model.DatasetRef;
import com.cmp.core.model.SimilarityScore;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** 两两相似度缓存（按最近版本） */
public final class PairwiseCacheRepo {
    private static final class Key {
        final DatasetRef a,b;
        Key(DatasetRef x, DatasetRef y){ // 排序，保证对称
            if (x.toString().compareTo(y.toString())<=0){ a=x; b=y; } else { a=y; b=x; }
        }
        @Override public boolean equals(Object o){ return (o instanceof Key) && a.equals(((Key)o).a) && b.equals(((Key)o).b); }
        @Override public int hashCode(){ return a.hashCode()*31 + b.hashCode(); }
    }
    private final Map<Key, SimilarityScore> map = new ConcurrentHashMap<>();
    public void put(DatasetRef A, DatasetRef B, SimilarityScore s){ map.put(new Key(A,B), s); }
    public Optional<SimilarityScore> get(DatasetRef A, DatasetRef B){ return Optional.ofNullable(map.get(new Key(A,B))); }
}
