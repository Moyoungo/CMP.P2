package com.cmp.sim.normalize;

/** 将距离/散度转为相似度到 [0,1] 的简单映射 */
public final class Normalizer {
    private Normalizer(){}
    public static double fromCos(double cos){ return (cos + 1.0) / 2.0; }
    public static double fromPositive(double v){ return 1.0 / (1.0 + Math.max(0.0, v)); }
}
