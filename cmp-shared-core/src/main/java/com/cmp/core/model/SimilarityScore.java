package com.cmp.core.model;
public final class SimilarityScore {
    public final DatasetRef A;
    public final DatasetRef B;
    public final double sCos;
    public final double sL2;
    public final double sKl;
    public final double sMmd;
    public final double fused; // 0..100
    public SimilarityScore(DatasetRef a, DatasetRef b, double sCos, double sL2, double sKl, double sMmd, double fused) {
        this.A = a; this.B = b; this.sCos = sCos; this.sL2 = sL2; this.sKl = sKl; this.sMmd = sMmd; this.fused = fused;
    }
}
