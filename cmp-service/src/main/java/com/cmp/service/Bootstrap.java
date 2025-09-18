package com.cmp.service;
import com.cmp.service.config.ServiceConfig;
import com.cmp.service.repo.*;
import com.cmp.service.apiimpl.*;
import com.cmp.service.sim.SimilarityComputer;

/** 纯内存 Bootstrap（非网络），用于测试把实现类装配起来使用 */
public final class Bootstrap {
    public final SourceRegistry sourceRegistry = new SourceRegistry();
    public final GrantRegistryRepo grantRegistry = new GrantRegistryRepo();
    public final StatsRepo statsRepo = new StatsRepo();
    public final PairwiseCacheRepo pairwiseCache = new PairwiseCacheRepo();
    public final TeeCommitRepo teeCommitRepo = new TeeCommitRepo();
    public final ServiceConfig cfg = ServiceConfig.defaults();
    public final RegistrationApiImpl registration = new RegistrationApiImpl(sourceRegistry);
    public final GrantApiImpl grant = new GrantApiImpl(sourceRegistry, grantRegistry);
    public final VectorIngestApiImpl vectorIngest = new VectorIngestApiImpl(statsRepo, teeCommitRepo, cfg);
    public final SimilarityApiImpl similarity = new SimilarityApiImpl(statsRepo, pairwiseCache, new SimilarityComputer(cfg));
}
