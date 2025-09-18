package com.cmp.cloud.api;
import com.cmp.protocol.api.StateTableApi;
import com.cmp.protocol.dto.StateTableDTO;
import com.cmp.cloud.repo.StateTableRepo;

/** 云端：状态表 S 的实现（内存版） */
public final class StateTableApiImpl implements StateTableApi {
    private final StateTableRepo repo;
    public StateTableApiImpl(StateTableRepo repo){ this.repo = repo; }
    @Override public void put(StateTableDTO.PutRequest req) {
        repo.put(req.h1st, req.sk); // sk 可为 null → 断点
    }
    @Override public StateTableDTO.GetResponse get(StateTableDTO.GetRequest req) {
        return new StateTableDTO.GetResponse(repo.get(req.h1st));
    }
}
