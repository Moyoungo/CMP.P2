package com.cmp.service.apiimpl;
import com.cmp.protocol.api.VectorIngestApi;
import com.cmp.protocol.dto.VectorBatchDTO;
import com.cmp.protocol.types.Ack;
import com.cmp.core.model.DatasetRef;
import com.cmp.service.repo.StatsRepo;
import com.cmp.service.repo.TeeCommitRepo;
import com.cmp.service.config.ServiceConfig;
import com.cmp.sim.accumulator.Accumulator;
import com.cmp.core.rff.RFF;

/** 接收向量批次并更新 Accumulator（含 RFF 特征） */
public final class VectorIngestApiImpl implements VectorIngestApi {
    private final StatsRepo stats;
    private final TeeCommitRepo commits;
    private final ServiceConfig cfg;
    // 为每个 (sid,ds) 懒加载一个 RFF（输入维度由首个向量决定）
    private final java.util.Map<DatasetRef, RFF> rffMap = new java.util.concurrent.ConcurrentHashMap<>();

    public VectorIngestApiImpl(StatsRepo stats, TeeCommitRepo commits, ServiceConfig cfg){
        this.stats = stats;
        this.commits = commits;
        this.cfg = cfg;
    }
    @Override public Ack postVectorBatch(VectorBatchDTO batch) {
        if (batch.vecs.isEmpty()) return Ack.ok("empty batch");
        int d = batch.vecs.get(0).length;
        DatasetRef ref = new DatasetRef(batch.sid, batch.ds);
        RFF rff = rffMap.computeIfAbsent(ref, k -> new RFF(d, cfg.rffDim, cfg.rffGamma));
        Accumulator acc = stats.getOrCreate(ref, d, cfg.rffDim);

        for (int i=0;i<batch.vecs.size();i++){
            float[] x = batch.vecs.get(i);
            float[] phi = rff.transform(x);
            acc.add(x, phi);
        }
        // 记录批次提交
        commits.put(new TeeCommitRepo.Entry(batch.sid, batch.ds, batch.t, batch.merkleRoot, batch.attestation));
        return Ack.ok("vectors ingested: "+batch.vecs.size());
    }
}
