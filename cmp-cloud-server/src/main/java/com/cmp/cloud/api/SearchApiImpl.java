package com.cmp.cloud.api;
import com.cmp.protocol.api.SearchApi;
import com.cmp.protocol.dto.SearchDTO;
import com.cmp.cloud.repo.IndexRepo;
import com.cmp.cloud.repo.StateTableRepo;
import com.cmp.core.crypto.HashUtil;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 云端：搜索实现（单批检索；是否“还有下一批”通过 S 表判断，由客户端前推）。
 * 兼容“直接购买关键字”的模式。
 */
public final class SearchApiImpl implements SearchApi {
    private final IndexRepo indexRepo;
    private final StateTableRepo stateRepo;
    public SearchApiImpl(IndexRepo indexRepo, StateTableRepo stateRepo){
        this.indexRepo = indexRepo;
        this.stateRepo = stateRepo;
    }
    @Override public SearchDTO.Response search(SearchDTO.Request req) {
        byte[] sidB = req.sid.getBytes(StandardCharsets.UTF_8);
        byte[] dsB  = req.ds.getBytes(StandardCharsets.UTF_8);
        byte[] h1st = HashUtil.H1(req.stNow);
        byte[] label = HashUtil.labelHash(sidB, dsB, req.tokenW, h1st);
        List<Integer> docs = indexRepo.get(label);
        boolean hasMore = (stateRepo.get(h1st) != null);
        return new SearchDTO.Response(docs, hasMore);
    }
}
