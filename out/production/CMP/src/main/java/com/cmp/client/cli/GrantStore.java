package com.cmp.client.cli;
import com.cmp.client.sdk.GrantSession;
import com.cmp.core.model.Grant;
import java.io.*;
import java.util.*;

/** 极简“本地授权存储”（纯文本 KV），避免引入第三方 JSON 依赖 */
public final class GrantStore {
    private final File file;
    public GrantStore(File file){ this.file = file; }
    public void save(GrantSession s) throws IOException {
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), java.nio.charset.StandardCharsets.UTF_8))) {
            pw.println("clientId="+s.clientId);
            pw.println("sid="+s.sid);
            pw.println("ds="+s.ds);
            pw.println("mode="+s.mode.name());
            pw.println("st0="+Base64.getEncoder().encodeToString(s.st0));
            pw.println("alpha="+Base64.getEncoder().encodeToString(s.alpha));
            if (s.mode == Grant.Mode.ALL_KEYWORDS) {
                pw.println("kLabel="+Base64.getEncoder().encodeToString(s.kLabel));
            } else {
                for (var e : s.perKeywordKLabels.entrySet()){
                    pw.println("kw."+e.getKey()+"="+Base64.getEncoder().encodeToString(e.getValue()));
                }
            }
        }
    }
    public GrantSession load() throws IOException {
        Properties p = new Properties();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), java.nio.charset.StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null){
                int i = line.indexOf('=');
                if (i>0) p.setProperty(line.substring(0,i), line.substring(i+1));
            }
        }
        String clientId = p.getProperty("clientId");
        String sid = p.getProperty("sid");
        String ds = p.getProperty("ds");
        com.cmp.core.model.Grant.Mode mode = com.cmp.core.model.Grant.Mode.valueOf(p.getProperty("mode"));
        byte[] st0 = Base64.getDecoder().decode(p.getProperty("st0"));
        byte[] alpha = Base64.getDecoder().decode(p.getProperty("alpha"));
        if (mode == com.cmp.core.model.Grant.Mode.ALL_KEYWORDS){
            byte[] kLabel = Base64.getDecoder().decode(p.getProperty("kLabel"));
            return new GrantSession(clientId, sid, ds, st0, alpha, mode, kLabel, null);
        } else {
            Map<String, byte[]> m = new HashMap<>();
            for (String k : p.stringPropertyNames()){
                if (k.startsWith("kw.")){
                    String w = k.substring(3);
                    m.put(w, Base64.getDecoder().decode(p.getProperty(k)));
                }
            }
            return new GrantSession(clientId, sid, ds, st0, alpha, mode, null, m);
        }
    }
    private static final class Base64 {
        static java.util.Base64.Encoder getEncoder(){ return java.util.Base64.getEncoder(); }
        static java.util.Base64.Decoder getDecoder(){ return java.util.Base64.getDecoder(); }
    }
}
