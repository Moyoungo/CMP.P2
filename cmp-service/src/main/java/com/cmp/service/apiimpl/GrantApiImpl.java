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
        this.sources = sources; this.grants = grants;
    }
    @Override public GrantContract.Response issueContract(GrantContract.Request req) {
        byte[] ksrc = sources.get(req.sid);
        if (ksrc == null) throw new IllegalArgumentException("unknown sid");

        byte[] st0 = new byte[32];
        new SecureRandom().nextBytes(st0);
        byte[] alpha = Derive.alpha(ksrc, req.sid, req.ds, req.clientId);

        if (req.mode == Grant.Mode.ALL_KEYWORDS){
            byte[] kLabelDS = Derive.kLabel(ksrc, req.sid, req.ds, req.clientId);
            var g = new Grant(req.clientId, req.sid, req.ds, new State(st0), alpha, kLabelDS);
            grants.put(g);
            return new GrantContract.Response(req.clientId, req.sid, req.ds, st0, alpha, req.mode, kLabelDS, null);
        } else {
            Map<String, byte[]> perWordTokens = new HashMap<>();
            for (String w : req.allowedKeywords){
                perWordTokens.put(w, Derive.kLabelPerWord(ksrc, req.sid, req.ds, req.clientId, w)); // 这里是 tokenW
            }
            var g = new Grant(req.clientId, req.sid, req.ds, new State(st0), alpha,
                    Grant.Mode.PER_KEYWORD, null, perWordTokens);
            grants.put(g);
            return new GrantContract.Response(req.clientId, req.sid, req.ds, st0, alpha, req.mode, null, perWordTokens);
        }
    }
}
