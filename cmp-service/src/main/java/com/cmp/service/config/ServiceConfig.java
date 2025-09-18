package com.cmp.service.config;

/** 服务配置：权重、RFF 维度与 gamma、触发阈值 */
public final class ServiceConfig {
    public final double wCos, wL2, wKl, wMmd;
    public final int rffDim;
    public final float rffGamma;
    public final double percentThreshold;
    public final double driftTau;

    public ServiceConfig(double wCos, double wL2, double wKl, double wMmd,
                         int rffDim, float rffGamma, double percentThreshold, double driftTau) {
        double s = wCos + wL2 + wKl + wMmd;
        this.wCos = wCos/s; this.wL2 = wL2/s; this.wKl = wKl/s; this.wMmd = wMmd/s;
        this.rffDim = rffDim; this.rffGamma = rffGamma;
        this.percentThreshold = percentThreshold; this.driftTau = driftTau;
    }
    public static ServiceConfig defaults(){
        return new ServiceConfig(0.4,0.2,0.2,0.2, 256, 1.0f, 0.01, 0.005);
    }
}
