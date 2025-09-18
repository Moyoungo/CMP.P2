package com.cmp.sim.accumulator;
import java.util.Arrays;

/** 可增量统计：n, s, s2, sphi（RFF），并保留向量维度 d 与 RFF 维度 m */
public final class Accumulator {
    private int n;
    private final int d;
    private final int m;
    private final float[] s;
    private final float[] s2;
    private final float[] sphi;
    private int rev;
    private int deltaN;
    private float[] muOld; // 上次基线的均值（用于漂移判定）

    public Accumulator(int dim, int rffDim){
        this.d = dim;
        this.m = rffDim;
        this.s = new float[dim];
        this.s2 = new float[dim];
        this.sphi = new float[rffDim];
        this.n = 0;
        this.rev = 0;
        this.deltaN = 0;
        this.muOld = new float[dim];
    }
    public void add(float[] x, float[] phi){
        if (x.length != d) throw new IllegalArgumentException("dim mismatch");
        if (phi.length != m) throw new IllegalArgumentException("rff dim mismatch");
        for (int i=0;i<d;i++){ s[i]+=x[i]; s2[i]+=x[i]*x[i]; }
        for (int i=0;i<m;i++){ sphi[i]+=phi[i]; }
        n++; deltaN++; rev++;
    }
    public int n(){ return n; }
    public int d(){ return d; }
    public int m(){ return m; }
    public int rev(){ return rev; }
    public int deltaN(){ return deltaN; }
    public void resetDelta(){ deltaN = 0; muOld = mean(); }
    public float[] s(){ return s; }
    public float[] s2(){ return s2; }
    public float[] sphi(){ return sphi; }
    public float[] mean(){
        float[] mu = new float[d];
        if (n==0) return mu;
        for (int i=0;i<d;i++) mu[i] = s[i]/n;
        return mu;
    }
    public float[] varDiag(){
        float[] v = new float[d];
        if (n==0) return v;
        for (int i=0;i<d;i++){
            float mu = s[i]/n;
            v[i] = s2[i]/n - mu*mu;
            if (v[i] < 1e-12f) v[i] = 1e-12f;
        }
        return v;
    }
    public float relMeanDrift(){
        float[] mu = mean();
        double num=0, den=0;
        for (int i=0;i<d;i++){ double diff=mu[i]-muOld[i]; num+=diff*diff; den+=muOld[i]*muOld[i]; }
        if (den < 1e-12) den = 1.0;
        return (float)Math.sqrt(num/den);
    }
}
