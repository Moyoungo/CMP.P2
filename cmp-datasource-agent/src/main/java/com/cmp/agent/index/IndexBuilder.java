package com.cmp.agent.index;
import com.cmp.core.crypto.HashUtil;
import com.cmp.core.crypto.HmacUtil;
import com.cmp.core.model.PostingEntry;
import com.cmp.core.model.State;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** 根据 (sid,ds,t, ST_t, K_label, keywords) 生成 label->docIds 的增量条目 */
public final class IndexBuilder {
    public static List<PostingEntry> build(String sid, String ds, int t, State st, byte[] kLabel,
                                           List<com.cmp.agent.model.Document> docs){
        Map<String, List<Integer>> kwToDocs = new HashMap<>();
        for (com.cmp.agent.model.Document d : docs){
            for (String w : d.keywords){
                kwToDocs.computeIfAbsent(w, k -> new ArrayList<>()).add(d.docId);
            }
        }
        byte[] sidB = sid.getBytes(StandardCharsets.UTF_8);
        byte[] dsB  = ds.getBytes(StandardCharsets.UTF_8);
        byte[] h1st = HashUtil.H1(st.bytes());
        List<PostingEntry> entries = new ArrayList<>();
        for (Map.Entry<String,List<Integer>> e : kwToDocs.entrySet()){
            String w = e.getKey();
            byte[] tokenW = HmacUtil.hmacSha256(kLabel, w.getBytes(StandardCharsets.UTF_8));
            byte[] label = HashUtil.labelHash(sidB, dsB, tokenW, h1st);
            entries.add(new PostingEntry(sid, ds, t, label, e.getValue()));
        }
        return entries;
    }
}
