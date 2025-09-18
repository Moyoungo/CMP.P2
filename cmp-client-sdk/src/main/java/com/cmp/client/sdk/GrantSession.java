package com.cmp.client.sdk;
import com.cmp.core.model.Grant;
import java.util.Map;
import java.util.Objects;

/** 封装一次授权（起始 ST、alpha、模式与标签密钥），客户端持有它来派生 token 与推进状态。 */
public final class GrantSession {
    public final String clientId;
    public final String sid;
    public final String ds;
    public final byte[] st0;
    public final byte[] alpha;
    public final Grant.Mode mode;
    public final byte[] kLabel; // ALL_KEYWORDS
    public final Map<String, byte[]> perKeywordKLabels; // PER_KEYWORD
    
    public GrantSession(String clientId, String sid, String ds, byte[] st0, byte[] alpha, Grant.Mode mode, byte[] kLabel, Map<String, byte[]> perKeywordKLabels){
        this.clientId = Objects.requireNonNull(clientId);
        this.sid = Objects.requireNonNull(sid);
        this.ds = Objects.requireNonNull(ds);
        this.st0 = Objects.requireNonNull(st0);
        this.alpha = Objects.requireNonNull(alpha);
        this.mode = Objects.requireNonNull(mode);
        this.kLabel = kLabel;
        this.perKeywordKLabels = perKeywordKLabels;
    }
}
