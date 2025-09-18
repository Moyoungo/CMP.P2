package com.cmp.core.model;
import java.util.List;
public final class SearchResult {
    public final List<Integer> docIds;
    public final boolean hasMore;
    public SearchResult(List<Integer> docIds, boolean hasMore) {
        this.docIds = docIds; this.hasMore = hasMore;
    }
}
