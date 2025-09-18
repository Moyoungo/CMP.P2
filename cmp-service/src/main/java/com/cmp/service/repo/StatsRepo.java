package com.cmp.service.repo;
import com.cmp.sim.accumulator.Accumulator;
import com.cmp.core.model.DatasetRef;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/** 每个数据集一个 Accumulator */
public final class StatsRepo {
    private final Map<DatasetRef, Accumulator> map = new ConcurrentHashMap<>();
    public Accumulator getOrCreate(DatasetRef ref, int dim, int rffDim){
        return map.computeIfAbsent(ref, k -> new Accumulator(dim, rffDim));
    }
    public Accumulator get(DatasetRef ref){ return map.get(ref); }
    public Map<DatasetRef, Accumulator> all(){ return map; }
}
