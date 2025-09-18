package com.cmp.core.vec;
public final class VecOps {
    private VecOps(){}
    public static float dot(float[] a, float[] b) {
        if (a.length != b.length) throw new IllegalArgumentException("dim mismatch");
        float s = 0f;
        for (int i = 0; i < a.length; i++) s += a[i]*b[i];
        return s;
    }
    public static float norm(float[] a) { return (float)Math.sqrt(dot(a,a)); }
    public static float[] hadamard(float[] a, float[] b) {
        if (a.length != b.length) throw new IllegalArgumentException("dim mismatch");
        float[] r = new float[a.length];
        for (int i=0;i<a.length;i++) r[i] = a[i]*b[i];
        return r;
    }
    public static float[] square(float[] a) {
        float[] r = new float[a.length];
        for (int i=0;i<a.length;i++) r[i] = a[i]*a[i];
        return r;
    }
}
