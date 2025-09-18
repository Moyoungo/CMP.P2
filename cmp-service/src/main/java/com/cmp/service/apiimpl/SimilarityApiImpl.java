package com.cmp.service.apiimpl;
import com.cmp.protocol.api.SimilarityApi;
import com.cmp.protocol.dto.SimilarityDTO;
import com.cmp.core.model.DatasetRef;
import com.cmp.core.model.SimilarityScore;
import com.cmp.service.repo.StatsRepo;
import com.cmp.service.repo.PairwiseCacheRepo;
import com.cmp.service.sim.SimilarityComputer;
import com.cmp.sim.accumulator.Accumulator;
import java.util.ArrayList;
import java.util.List;

/** 两两相似度：按需计算（也可以在触发策略下异步刷新缓存） */
public final class SimilarityApiImpl implements SimilarityApi {
    private final StatsRepo stats;
    private final PairwiseCacheRepo cache;
    private final SimilarityComputer computer;
    public SimilarityApiImpl(StatsRepo stats, PairwiseCacheRepo cache, SimilarityComputer computer){
        this.stats = stats; this.cache = cache; this.computer = computer;
    }
    @Override public SimilarityDTO.Response pairwise(SimilarityDTO.Request req) {
        List<SimilarityDTO.Pair> pairs = new ArrayList<>();
        List<DatasetRef> refs = req.refs;
        for (int i=0;i<refs.size();i++){
            for (int j=i+1;j<refs.size();j++){
                DatasetRef A = refs.get(i), B = refs.get(j);
                var cached = cache.get(A,B);
                SimilarityScore s;
                if (cached.isPresent()){
                    s = cached.get();
                } else {
                    Accumulator accA = stats.get(A), accB = stats.get(B);
                    if (accA == null || accB == null) continue;
                    s = computer.compute(A, accA, B, accB);
                    cache.put(A,B,s);
                }
                pairs.add(new SimilarityDTO.Pair(s.A, s.B, s.sCos, s.sL2, s.sKl, s.sMmd, s.fused));
            }
        }
        return new SimilarityDTO.Response(pairs);
    }
}
