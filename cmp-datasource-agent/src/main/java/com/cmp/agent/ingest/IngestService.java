package com.cmp.agent.ingest;
import com.cmp.agent.encrypt.Encryptor;
import com.cmp.agent.index.IndexBuilder;
import com.cmp.agent.model.Document;
import com.cmp.agent.util.Attestation;
import com.cmp.agent.util.Merkle;
import com.cmp.agent.vector.SimpleVectorizer;
import com.cmp.agent.sender.Sender;
import com.cmp.core.crypto.HashUtil;
import com.cmp.core.model.PostingEntry;
import com.cmp.core.model.State;
import com.cmp.protocol.dto.VectorBatchDTO;
import com.cmp.protocol.types.Ack;
import java.util.ArrayList;
import java.util.List;

/** 数据源侧一次批次的全流程：向量化→加密→建索引→上送云端与CMP */
public final class IngestService {
    private final Encryptor encryptor;
    private final SimpleVectorizer vectorizer;
    private final Sender sender;

    public IngestService(Encryptor encryptor, SimpleVectorizer vectorizer, Sender sender){
        this.encryptor = encryptor;
        this.vectorizer = vectorizer;
        this.sender = sender;
    }

    public Ack runBatch(String sid, String ds, int t, State st, byte[] kLabel, List<Document> docs){
        // 向量化 + H(doc)
        List<byte[]> hdocs = new ArrayList<>(docs.size());
        List<float[]> vecs = new ArrayList<>(docs.size());
        for (Document d : docs){
            byte[] h = HashUtil.H(d.bytes());
            hdocs.add(h);
            vecs.add(vectorizer.embed(d.text));
        }
        byte[] merkleRoot = Merkle.root(hdocs);
        byte[] att = Attestation.fake();

        // 加密（AAD 含 H(doc)）
        var blobs = encryptor.encryptBatch(sid, ds, t, docs);
        sender.sendCiphertexts(sid, ds, t, blobs);

        // 索引增量
        List<PostingEntry> entries = IndexBuilder.build(sid, ds, t, st, kLabel, docs);
        sender.sendIndexDelta(entries);

        // 向量批次
        var batch = new VectorBatchDTO(sid, ds, t, hdocs, vecs, merkleRoot, att);
        return sender.sendVectorBatch(batch);
    }
}
