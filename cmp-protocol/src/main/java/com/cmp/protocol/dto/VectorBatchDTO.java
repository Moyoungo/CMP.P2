package com.cmp.protocol.dto;
import java.util.List;

public final class VectorBatchDTO {
    public final String sid;
    public final String ds;
    public final int t;
    public final List<byte[]> hdocs;
    public final List<float[]> vecs;
    public final byte[] merkleRoot;
    public final byte[] attestation;
    public VectorBatchDTO(String sid, String ds, int t, List<byte[]> hdocs, List<float[]> vecs, byte[] merkleRoot, byte[] attestation) {
        this.sid=sid; this.ds=ds; this.t=t; this.hdocs=hdocs; this.vecs=vecs; this.merkleRoot=merkleRoot; this.attestation=attestation;
    }
}
