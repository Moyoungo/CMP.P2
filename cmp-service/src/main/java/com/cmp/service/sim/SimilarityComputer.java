package com.cmp.service.sim;
import com.cmp.core.model.DatasetRef;
import com.cmp.core.model.SimilarityScore;
import com.cmp.sim.accumulator.Accumulator;
import com.cmp.sim.metrics.Metrics;
import com.cmp.sim.normalize.Normalizer;
import com.cmp.service.config.ServiceConfig;

public final class SimilarityComputer {
    private final ServiceConfig cfg;
    public SimilarityComputer(ServiceConfig cfg){ this.cfg = cfg; }

    public SimilarityScore compute(DatasetRef Aref, Accumulator A, DatasetRef Bref, Accumulator B){
        double cos = Metrics.cosineMean(A,B);
        double S_cos = Normalizer.fromCos(cos);
        double l2 = Metrics.expectedL2Squared(A,B);
        double S_l2 = Normalizer.fromPositive(l2);
        double kl = Metrics.symKL(A,B);
        double S_kl = Normalizer.fromPositive(kl);
        double mmd = Metrics.mmd2Rff(A,B);
        double S_mmd = Normalizer.fromPositive(mmd);
        double fused = 100.0 * (cfg.wCos*S_cos + cfg.wL2*S_l2 + cfg.wKl*S_kl + cfg.wMmd*S_mmd);
        return new SimilarityScore(Aref, Bref, S_cos, S_l2, S_kl, S_mmd, fused);
    }
}
