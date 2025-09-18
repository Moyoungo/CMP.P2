package com.cmp.protocol.api;
import com.cmp.protocol.dto.VectorBatchDTO;
import com.cmp.protocol.types.Ack;
/** CMP：接收向量批次（含一致性证明材料） */
public interface VectorIngestApi {
    Ack postVectorBatch(VectorBatchDTO batch);
}
