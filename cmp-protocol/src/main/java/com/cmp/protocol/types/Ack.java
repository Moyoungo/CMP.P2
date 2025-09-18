package com.cmp.protocol.types;
public final class Ack {
    public final boolean ok;
    public final String message;
    public Ack(boolean ok, String message) { this.ok = ok; this.message = message; }
    public static Ack ok(){ return new Ack(true, "OK"); }
    public static Ack ok(String msg){ return new Ack(true, msg); }
    public static Ack fail(String msg){ return new Ack(false, msg); }
}
