package com.cmp.core.model;
public final class RangeSpec {
    public final int tStart;
    public final Integer tEnd;   // 可为 null
    public final boolean openEnd;
    public RangeSpec(int tStart, Integer tEnd, boolean openEnd) {
        if (tStart < 0) throw new IllegalArgumentException("tStart>=0");
        if (tEnd != null && tEnd < tStart) throw new IllegalArgumentException("tEnd>=tStart");
        this.tStart = tStart; this.tEnd = tEnd; this.openEnd = openEnd;
    }
    public static RangeSpec closed(int tStart, int tEnd){ return new RangeSpec(tStart, tEnd, false); }
    public static RangeSpec openEnd(int tStart){ return new RangeSpec(tStart, null, true); }
    @Override public String toString(){ return openEnd? ("["+tStart+", …]") : ("["+tStart+","+tEnd+"]"); }
}
