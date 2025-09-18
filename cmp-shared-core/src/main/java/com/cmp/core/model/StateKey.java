package com.cmp.core.model;
import java.util.Arrays;
public final class StateKey {
    private final byte[] value; // 允许为 null
    public static final StateKey NULL = new StateKey(null);
    public StateKey(byte[] value){ this.value = value; }
    public boolean isNull(){ return value == null; }
    public byte[] bytes(){ return value; }
    @Override public String toString(){ return isNull()? "SK[⊥]" : "SK[len="+value.length+"]"; }
    @Override public boolean equals(Object o){ return (o instanceof StateKey) && java.util.Arrays.equals(value, ((StateKey)o).value); }
    @Override public int hashCode(){ return Arrays.hashCode(value); }
}
