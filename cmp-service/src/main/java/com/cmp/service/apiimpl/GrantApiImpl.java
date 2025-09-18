package com.cmp.service.apiimpl;
import com.cmp.protocol.api.GrantApi;
import com.cmp.protocol.dto.GrantContract;
import com.cmp.core.model.Grant;
import com.cmp.core.model.State;
import com.cmp.service.repo.SourceRegistry;
import com.cmp.service.repo.GrantRegistryRepo;
import com.cmp.service.keys.Derive;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/** 发放授权：支持 ALL_KEYWORDS 与 PER_KEYWORD（直接购买某关键字） */
public final class GrantApiImpl implements GrantApi {
    private final SourceRegistry sources;
    private final GrantRegistryRepo grants;
    public GrantApiImpl(SourceRegistry sources, GrantRegistryRepo grants){
        this.sources = sources;
        this.grants = grants;
    }
    @Override public GrantContract.Response issueContract(GrantContract.Request req) {
        byte[] ksrc = sources.get(req.sid);
        if (ksrc == null) throw new IllegalArgumentException("unknown sid");
        // 生成起始状态 ST_{t0} 与 α（此处随机；生产需与云端状态链对齐）
        byte[] st0 = new byte[32]; new SecureRandom().nextBytes(st0);
        byte[] alpha = Derive.alpha(ksrc, req.sid, req.ds, req.clientId);

        Grant grant;
        if (req.mode == Grant.Mode.ALL_KEYWORDS){
            byte[] kLabel = Derive.kLabel(ksrc, req.sid, req.ds, req.clientId);
            grant = new Grant(req.clientId, req.sid, req.ds, new State(st0), alpha, kLabel);
            grants.put(grant);
            return new GrantContract.Response(req.clientId, req.sid, req.ds, st0, alpha, req.mode, kLabel, null);
        }else{
            Map<String, byte[]> m = new HashMap<>();
            for (String w : req.allowedKeywords){
                m.put(w, Derive.kLabelPerWord(ksrc, req.sid, req.ds, req.clientId, w));
            }
            grant = new Grant(req.clientId, req.sid, req.ds, new State(st0), alpha, Grant.Mode.PER_KEYWORD, null, m);
            grants.put(grant);
            return new GrantContract.Response(req.clientId, req.sid, req.ds, st0, alpha, req.mode, null, m);
        }
    }
}
