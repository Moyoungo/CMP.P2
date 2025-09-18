package com.cmp.protocol.dto;
import com.cmp.core.model.Grant;
import com.cmp.core.model.RangeSpec;
import java.util.List;
import java.util.Map;

public final class GrantContract {
    public static final class Request {
        public final String clientId;
        public final String sid;
        public final String ds;
        public final RangeSpec range;
        public final Grant.Mode mode;
        public final List<String> allowedKeywords; // PER_KEYWORD 时使用
        public Request(String clientId, String sid, String ds, RangeSpec range, Grant.Mode mode, List<String> allowedKeywords) {
            this.clientId = clientId; this.sid = sid; this.ds = ds; this.range = range; this.mode = mode; this.allowedKeywords = allowedKeywords;
        }
    }
    public static final class Response {
        public final String clientId; public final String sid; public final String ds;
        public final byte[] st0; public final byte[] alpha;
        public final Grant.Mode mode;
        public final byte[] kLabel; // ALL_KEYWORDS 模式下返回
        public final Map<String, byte[]> perKeywordKLabels; // PER_KEYWORD 模式下返回
        public Response(String clientId, String sid, String ds, byte[] st0, byte[] alpha, Grant.Mode mode, byte[] kLabel, Map<String,byte[]> perKeywordKLabels) {
            this.clientId=clientId; this.sid=sid; this.ds=ds; this.st0=st0; this.alpha=alpha; this.mode=mode; this.kLabel=kLabel; this.perKeywordKLabels=perKeywordKLabels;
        }
    }
}
