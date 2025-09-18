package com.cmp.cloud.util;
import java.util.Arrays;
public final class ByteArrayWrapper {
    public final byte[] bytes;
    private final int hash;
    public ByteArrayWrapper(byte[] bytes){
        this.bytes = bytes;
        this.hash = Arrays.hashCode(bytes);
    }
    @Override public boolean equals(Object o){
        if(!(o instanceof ByteArrayWrapper)) return false;
        return Arrays.equals(bytes, ((ByteArrayWrapper)o).bytes);
    }
    @Override public int hashCode(){ return hash; }
}
