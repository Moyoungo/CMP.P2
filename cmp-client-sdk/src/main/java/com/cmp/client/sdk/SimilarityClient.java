package com.cmp.client.sdk;
import com.cmp.protocol.api.SimilarityApi;
import com.cmp.protocol.dto.SimilarityDTO;
import com.cmp.core.model.DatasetRef;
import java.util.List;

/** 相似度客户端（可选） */
public final class SimilarityClient {
    private final SimilarityApi api;
    public SimilarityClient(SimilarityApi api){ this.api = api; }
    public List<SimilarityDTO.Pair> pairwise(List<DatasetRef> refs){
        return api.pairwise(new SimilarityDTO.Request(refs)).pairs;
    }
}
