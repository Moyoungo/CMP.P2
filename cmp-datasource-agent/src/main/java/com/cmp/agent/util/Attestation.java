package com.cmp.agent.util;
/** 占位的“证明”字节：真实实现可替换为容器签名/TEE 报告 */
public final class Attestation {
    public static byte[] fake(){ return "FAKE_ATTESTATION".getBytes(); }
}
