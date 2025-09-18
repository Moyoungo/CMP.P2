package com.cmp.agent.sender;
import com.cmp.protocol.api.CloudIndexApi;
import com.cmp.protocol.api.VectorIngestApi;
import com.cmp.protocol.dto.UploadIndexDTO;
import com.cmp.protocol.dto.VectorBatchDTO;
import com.cmp.protocol.types.Ack;
import com.cmp.core.model.PostingEntry;
import java.util.List;

/** 发送器：此版本为“本地适配”，直接调用接口；网络化时替换为 REST/gRPC 客户端 */
public final class Sender {
    private final CloudIndexApi cloud;
    private final VectorIngestApi cmp;
    public Sender(CloudIndexApi cloud, VectorIngestApi cmp){
        this.cloud = cloud;
        this.cmp = cmp;
    }
    public Ack sendCiphertexts(String sid, String ds, int t, List<byte[]> blobs){
        return cloud.uploadCiphertexts(new UploadIndexDTO.CiphertextsRequest(sid, ds, t, blobs));
    }
    public Ack sendIndexDelta(List<com.cmp.core.model.PostingEntry> entries){
        return cloud.uploadIndexDelta(new UploadIndexDTO.IndexDeltaRequest(entries));
    }
    public Ack sendVectorBatch(VectorBatchDTO batch){
        return cmp.postVectorBatch(batch);
    }
}
