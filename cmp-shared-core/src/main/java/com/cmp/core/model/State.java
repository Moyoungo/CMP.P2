package com.cmp.core.model;
import java.util.Arrays;
import java.util.Objects;

/** 状态值 ST_t（二进制随机串） */
public final class State {
    private final byte[] value;
    public State(byte[] value) { this.value = Objects.requireNonNull(value); }
    public byte[] bytes(){ return value; }
    public int length(){ return value.length; }
    @Override public String toString(){ return "State[len="+value.length+"]"; }
    @Override public boolean equals(Object o){ return (o instanceof State) && Arrays.equals(value, ((State)o).value); }
    @Override public int hashCode(){ return Arrays.hashCode(value); }
}
