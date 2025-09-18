package com.cmp.sim.trigger;
import com.cmp.sim.accumulator.Accumulator;

/** 触发策略：累计变更达 1% 或相对均值漂移超过阈值 */
public final class TriggerPolicy {
    private final double percentThreshold;
    private final double driftTau;
    public TriggerPolicy(double percentThreshold, double driftTau){
        this.percentThreshold = percentThreshold;
        this.driftTau = driftTau;
    }
    public boolean shouldRefresh(Accumulator acc, int n0){
        boolean byCount = acc.deltaN() >= Math.max(1, (int)Math.ceil(percentThreshold * Math.max(1, n0)));
        boolean byDrift = acc.relMeanDrift() >= driftTau;
        return byCount || byDrift;
    }
}
