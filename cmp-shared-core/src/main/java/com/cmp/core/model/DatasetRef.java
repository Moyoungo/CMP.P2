package com.cmp.core.model;
import java.util.Objects;
public final class DatasetRef {
    public final String sid;
    public final String ds;
    public DatasetRef(String sid, String ds) {
        this.sid = Objects.requireNonNull(sid);
        this.ds = Objects.requireNonNull(ds);
    }
    @Override public boolean equals(Object o){
        if(!(o instanceof DatasetRef)) return false;
        DatasetRef d=(DatasetRef)o;
        return sid.equals(d.sid) && ds.equals(d.ds);
    }
    @Override public int hashCode(){ return sid.hashCode()*31 + ds.hashCode(); }
    @Override public String toString(){ return sid+":"+ds; }
}
