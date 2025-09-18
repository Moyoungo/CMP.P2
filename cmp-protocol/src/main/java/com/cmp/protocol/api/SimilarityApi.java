package com.cmp.protocol.api;
import com.cmp.protocol.dto.SimilarityDTO;
/** CMP：相似度查询（可选） */
public interface SimilarityApi {
    SimilarityDTO.Response pairwise(SimilarityDTO.Request req);
}
