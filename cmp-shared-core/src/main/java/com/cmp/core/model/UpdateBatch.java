package com.cmp.core.model;
import java.util.List;
import java.util.Objects;

/** 向 CMP 提交的向量批次（保证与云端密文一致）：包含 H(doc) 与向量 */
public final class UpdateBatch {
    public final String sid;
    public final String ds;
    public final int t;                 // 批次号
    public final List<byte[]> hdocs;    // 文档哈希（与密文 AAD 一致）
    public final List<float[]> vecs;    // 文本向量
    public final byte[] merkleRoot;     // H(doc) 的 Merkle 根
    public final byte[] attestation;    // 可空（容器签名/TEE 证明）

    public UpdateBatch(String sid, String ds, int t, List<byte[]> hdocs, List<float[]> vecs, byte[] merkleRoot, byte[] attestation) {
        this.sid = Objects.requireNonNull(sid);
        this.ds = Objects.requireNonNull(ds);
        this.t = t;
        this.hdocs = Objects.requireNonNull(hdocs);
        this.vecs = Objects.requireNonNull(vecs);
        this.merkleRoot = Objects.requireNonNull(merkleRoot);
        this.attestation = attestation; // 可选
        if (hdocs.size() != vecs.size()) throw new IllegalArgumentException("hdocs.size != vecs.size");
        if (!hdocs.isEmpty()) {
            int d = vecs.get(0).length;
            for (float[] v : vecs) if (v.length != d) throw new IllegalArgumentException("vec dim inconsistent");
        }
    }
    public int size(){ return hdocs.size(); }
}
