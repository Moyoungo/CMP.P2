package com.cmp.cloud.api;
import com.cmp.protocol.api.CloudIndexApi;
import com.cmp.protocol.dto.UploadIndexDTO;
import com.cmp.protocol.types.Ack;
import com.cmp.cloud.repo.IndexRepo;
import com.cmp.cloud.repo.DocStoreRepo;

/** 云端：上传密文与索引增量的实现（内存版） */
public final class CloudIndexApiImpl implements CloudIndexApi {
    private final IndexRepo indexRepo;
    private final DocStoreRepo docRepo;
    public CloudIndexApiImpl(IndexRepo indexRepo, DocStoreRepo docRepo){
        this.indexRepo = indexRepo;
        this.docRepo = docRepo;
    }
    @Override public Ack uploadCiphertexts(UploadIndexDTO.CiphertextsRequest req) {
        docRepo.put(req.sid, req.ds, req.t, req.ciphertextBlobs);
        return Ack.ok("ciphertexts accepted: "+req.ciphertextBlobs.size());
    }
    @Override public Ack uploadIndexDelta(UploadIndexDTO.IndexDeltaRequest req) {
        indexRepo.applyDelta(req.entries);
        return Ack.ok("index delta applied: "+req.entries.size());
    }
}
