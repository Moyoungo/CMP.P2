package com.cmp.agent.encrypt;
import com.cmp.core.crypto.AesGcm;
import com.cmp.core.crypto.HashUtil;
import com.cmp.core.util.Bytes;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** 简单的 AES-GCM 加密器：将 sid,ds,t,docId,H(doc) 放到 AAD */
public final class Encryptor {
    private final byte[] key;
    public Encryptor(byte[] key){ this.key = key; }

    /** 返回每个文档的密文 blob（nonce || aadLen || aad || ciphertext） */
    public List<byte[]> encryptBatch(String sid, String ds, int t, List<com.cmp.agent.model.Document> docs){
        List<byte[]> out = new ArrayList<>();
        for (com.cmp.agent.model.Document d : docs){
            byte[] nonce = AesGcm.randomNonce();
            byte[] hdoc = HashUtil.H(d.bytes());
            byte[] aad = buildAad(sid, ds, t, d.docId, hdoc);
            byte[] ct  = AesGcm.encrypt(key, nonce, aad, d.bytes());
            byte[] aadLen = Bytes.fromInt(aad.length);
            out.add(Bytes.concat(nonce, aadLen, aad, ct));
        }
        return out;
    }
    private static byte[] buildAad(String sid, String ds, int t, int docId, byte[] hdoc){
        return com.cmp.core.util.Bytes.concat(
            sid.getBytes(StandardCharsets.UTF_8),
            new byte[]{0},
            ds.getBytes(StandardCharsets.UTF_8),
            new byte[]{0},
            com.cmp.core.util.Bytes.fromInt(t),
            com.cmp.core.util.Bytes.fromInt(docId),
            hdoc
        );
    }
}
