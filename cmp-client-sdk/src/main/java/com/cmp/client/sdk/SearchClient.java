package com.cmp.client.sdk;
import com.cmp.protocol.api.SearchApi;
import com.cmp.protocol.api.StateTableApi;
import com.cmp.protocol.dto.SearchDTO;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** 搜索客户端：按授权前推逐批检索，直到断点或达到批次数上限 */
public final class SearchClient {
    private final SearchApi searchApi;
    private final StateTableApi stateApi;
    public SearchClient(SearchApi searchApi, StateTableApi stateApi){
        this.searchApi = searchApi;
        this.stateApi = stateApi;
    }
    public Result searchAll(GrantSession grant, String keyword, int maxBatches){
        byte[] tokenW = TokenDeriver.tokenFor(grant, keyword);
        byte[] st = grant.st0;
        Set<Integer> acc = new LinkedHashSet<>();
        int count = 0;
        while (st != null && count < maxBatches){
            var req = new SearchDTO.Request(grant.sid, grant.ds, tokenW, st);
            var resp = searchApi.search(req);
            if (resp.docIds != null) acc.addAll(resp.docIds);
            if (!resp.hasMore) break;
            st = StateAdvance.next(stateApi, grant.sid, grant.ds, grant.alpha, st);
            count++;
        }
        return new Result(new ArrayList<>(acc), st != null);
    }
    public static final class Result {
        public final List<Integer> docIds;
        public final boolean mayHaveMore;
        public Result(List<Integer> docIds, boolean mayHaveMore){ this.docIds = docIds; this.mayHaveMore = mayHaveMore; }
    }
}
