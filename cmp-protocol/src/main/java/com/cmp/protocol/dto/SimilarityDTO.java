package com.cmp.protocol.dto;
import com.cmp.core.model.DatasetRef;
import java.util.List;

public final class SimilarityDTO {
    public static final class Request {
        public final List<DatasetRef> refs;
        public Request(List<DatasetRef> refs){ this.refs = refs; }
    }
    public static final class Pair {
        public final DatasetRef A; public final DatasetRef B;
        public final double sCos, sL2, sKl, sMmd, fused;
        public Pair(DatasetRef a, DatasetRef b, double sCos, double sL2, double sKl, double sMmd, double fused){
            this.A=a; this.B=b; this.sCos=sCos; this.sL2=sL2; this.sKl=sKl; this.sMmd=sMmd; this.fused=fused;
        }
    }
    public static final class Response {
        public final List<Pair> pairs;
        public Response(List<Pair> pairs){ this.pairs = pairs; }
    }
}
