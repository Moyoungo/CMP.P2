package com.cmp.protocol.api;
import com.cmp.protocol.dto.UploadIndexDTO;
import com.cmp.protocol.types.Ack;
/** 云端：上传密文与索引增量 */
public interface CloudIndexApi {
    Ack uploadCiphertexts(UploadIndexDTO.CiphertextsRequest req);
    Ack uploadIndexDelta(UploadIndexDTO.IndexDeltaRequest req);
}
