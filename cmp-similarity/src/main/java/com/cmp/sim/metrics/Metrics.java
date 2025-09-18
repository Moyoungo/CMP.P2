package com.cmp.sim.metrics;
import com.cmp.sim.accumulator.Accumulator;

/** 四项指标：Cosine(μ), Expected L2^2, Sym KL (diag), MMD^2 (RFF) */
public final class Metrics {
    private Metrics(){}

    public static double cosineMean(Accumulator A, Accumulator B){
        float[] a = A.mean(), b = B.mean();
        double dot=0, na=0, nb=0;
        for (int i=0;i<a.length;i++){ dot+=a[i]*b[i]; na+=a[i]*a[i]; nb+=b[i]*b[i]; }
        if (na==0 || nb==0) return 0.0;
        return dot / Math.sqrt(na*nb);
    }

    /** 期望平方欧氏距离 E||X−Y||^2 = ||μA−μB||^2 + tr(ΣA)+tr(ΣB) */
    public static double expectedL2Squared(Accumulator A, Accumulator B){
        float[] muA = A.mean(), muB = B.mean();
        double dmu2 = 0;
        for (int i=0;i<muA.length;i++){ double d=muA[i]-muB[i]; dmu2 += d*d; }
        double tr = 0;
        float[] vA = A.varDiag(), vB = B.varDiag();
        for (int i=0;i<vA.length;i++){ tr += vA[i] + vB[i]; }
        return dmu2 + tr;
    }

    /** 对称 KL（对角高斯近似） */
    public static double symKL(Accumulator A, Accumulator B){
        float[] muA = A.mean(), muB = B.mean();
        float[] s2A = A.varDiag(), s2B = B.varDiag();
        double klAB = 0, klBA = 0;
        for (int i=0;i<muA.length;i++){
            double va = s2A[i], vb = s2B[i];
            double d = muB[i]-muA[i];
            klAB += Math.log(Math.sqrt(vb/va)) + (va + d*d)/(2*vb) - 0.5;
            double d2 = muA[i]-muB[i];
            klBA += Math.log(Math.sqrt(va/vb)) + (vb + d2*d2)/(2*va) - 0.5;
        }
        return klAB + klBA;
    }

    /** MMD^2 (RFF 近似)：|| sφA/nA − sφB/nB ||^2 */
    public static double mmd2Rff(Accumulator A, Accumulator B){
        float[] a = A.sphi(), b = B.sphi();
        int nA = Math.max(1, A.n()), nB = Math.max(1, B.n());
        double s = 0;
        for (int i=0;i<a.length;i++){
            double da = a[i]/nA - b[i]/nB;
            s += da*da;
        }
        return s;
    }
}
