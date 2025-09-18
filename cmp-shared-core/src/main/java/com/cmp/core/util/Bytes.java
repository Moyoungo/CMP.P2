package com.cmp.core.util;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HexFormat;

/** 字节数组辅助工具（纯 JDK） */
public final class Bytes {
    private Bytes() {}

    public static byte[] concat(byte[]... parts) {
        int len = 0;
        for (byte[] p : parts) { if (p != null) len += p.length; }
        byte[] out = new byte[len];
        int pos = 0;
        for (byte[] p : parts) {
            if (p != null) {
                System.arraycopy(p, 0, out, pos, p.length);
                pos += p.length;
            }
        }
        return out;
    }

    public static byte[] fromInt(int v) {
        return ByteBuffer.allocate(4).putInt(v).array();
    }
    public static byte[] fromLong(long v) {
        return ByteBuffer.allocate(8).putLong(v).array();
    }
    public static int toInt(byte[] b) {
        if (b.length != 4) throw new IllegalArgumentException("len!=4");
        return ByteBuffer.wrap(b).getInt();
    }
    public static long toLong(byte[] b) {
        if (b.length != 8) throw new IllegalArgumentException("len!=8");
        return ByteBuffer.wrap(b).getLong();
    }
    public static byte[] copy(byte[] a) {
        return a == null ? null : Arrays.copyOf(a, a.length);
    }
    public static boolean isAllZero(byte[] a) {
        if (a == null) return false;
        for (byte v : a) if (v != 0) return false;
        return true;
    }
    public static String hex(byte[] a) {
        return a == null ? "null" : HexFormat.of().formatHex(a);
    }
}
