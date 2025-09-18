package com.cmp.core.model;
import java.util.Map;
import java.util.Objects;

public final class Grant {
    public enum Mode { ALL_KEYWORDS, PER_KEYWORD }
    public final String clientId;
    public final String sid;
    public final String ds;
    public final State startState;   // ST_{t0}
    public final byte[] alpha;       // 区间掩码 α
    public final Mode mode;
    public final byte[] kLabel;                 // ALL_KEYWORDS
    public final Map<String,byte[]> perKeywordLabelKeys; // PER_KEYWORD (w -> K_label(w))
    public Grant(String clientId, String sid, String ds, State startState, byte[] alpha, byte[] kLabel) {
        this(clientId, sid, ds, startState, alpha, Mode.ALL_KEYWORDS, kLabel, null);
    }
    public Grant(String clientId, String sid, String ds, State startState, byte[] alpha, Mode mode, byte[] kLabel, Map<String,byte[]> perKeywordLabelKeys) {
        this.clientId = Objects.requireNonNull(clientId);
        this.sid = Objects.requireNonNull(sid);
        this.ds = Objects.requireNonNull(ds);
        this.startState = Objects.requireNonNull(startState);
        this.alpha = Objects.requireNonNull(alpha);
        this.mode = Objects.requireNonNull(mode);
        this.kLabel = kLabel;
        this.perKeywordLabelKeys = perKeywordLabelKeys;
        if (mode == Mode.ALL_KEYWORDS && kLabel == null) throw new IllegalArgumentException("kLabel required");
        if (mode == Mode.PER_KEYWORD && (perKeywordLabelKeys == null || perKeywordLabelKeys.isEmpty())) throw new IllegalArgumentException("perKeywordLabelKeys required");
    }
}
