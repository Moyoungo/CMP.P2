package com.cmp.agent.vector;
import com.cmp.core.rff.RFF;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** 轻量向量器占位：将文本哈希累加到 dim，再过 RFF */
public final class SimpleVectorizer {
    private final int dim; // 输出维度（如 384/768）
    private final RFF rff;
    public SimpleVectorizer(int dim, int rffDim, float gamma){
        this.dim = dim;
        this.rff = new RFF(dim, rffDim, gamma);
    }
    public float[] embed(String text){
        byte[] b = text.getBytes(StandardCharsets.UTF_8);
        float[] v = new float[dim];
        for (int i=0;i<b.length;i++){
            int idx = (i * 1315423911) ^ (b[i] & 0xff);
            idx = (idx ^ (idx >>> 16)) & 0x7fffffff;
            v[idx % dim] += (b[i] & 0xff) / 255.0f;
        }
        return rff.transform(v);
    }
    public List<float[]> embedAll(List<String> texts){
        List<float[]> out = new ArrayList<>(texts.size());
        for (String t : texts) out.add(embed(t));
        return out;
    }
}
