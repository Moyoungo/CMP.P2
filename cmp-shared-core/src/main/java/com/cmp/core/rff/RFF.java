package com.cmp.core.rff;
import java.security.SecureRandom;

/** 随机傅里叶特征（RFF），支持单一 gamma（可多实例拼接）。 */
public final class RFF {
    private final int dimIn;
    private final int m; // 特征维度
    private final float gamma;
    private final float[][] W; // W ~ N(0, 2*gamma^2 I)
    private final float[] b;    // b ~ U[0, 2π]
    private static final float TWO_PI = (float)(2*Math.PI);

    public RFF(int dimIn, int m, float gamma) {
        this.dimIn = dimIn;
        this.m = m;
        this.gamma = gamma;
        this.W = new float[m][dimIn];
        this.b = new float[m];
        init();
    }
    private void init() {
        SecureRandom rnd = new SecureRandom();
        for (int i=0;i<m;i++) {
            for (int j=0;j<dimIn;j++) {
                float g = (float)(Math.sqrt(-2.0*Math.log(1.0 - rnd.nextDouble())) * Math.cos(2*Math.PI*rnd.nextDouble()));
                W[i][j] = (float)Math.sqrt(2)*gamma * g;
            }
            b[i] = rnd.nextFloat() * TWO_PI;
        }
    }
    /** φ(x) ∈ R^m */
    public float[] transform(float[] x) {
        if (x.length != dimIn) throw new IllegalArgumentException("dim mismatch");
        float[] z = new float[m];
        for (int i=0;i<m;i++) {
            float wx = 0f;
            for (int j=0;j<dimIn;j++) wx += W[i][j]*x[j];
            z[i] = (float)Math.cos(wx + b[i]);
        }
        return z;
    }
    public int getInputDim(){ return dimIn; }
    public int getFeatureDim(){ return m; }
    public float getGamma(){ return gamma; }
}
