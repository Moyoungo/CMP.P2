package com.cmp.protocol.dto;
public final class RegisterSource {
    public static final class Request {
        public final String sid;
        public Request(String sid){ this.sid = sid; }
    }
    public static final class Response {
        public final byte[] kSrc; // 源主密钥
        public Response(byte[] kSrc){ this.kSrc = kSrc; }
    }
}
