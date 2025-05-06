package com.nageoffer.project.utils;


import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecureShortLinkGenerator {
    private final SnowflakeIdGenerator snowflake;
    private final MessageDigest sha256;

    // Base62 字符表：索引 0–61
    private static final char[] BASE62 =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
                    .toCharArray();  // 共 62 字符

    public SecureShortLinkGenerator(int datacenterId, int workerId) {
        this.snowflake = new SnowflakeIdGenerator(datacenterId, workerId);
        try {
            this.sha256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 在标准 Java 实现中必定可用
            throw new RuntimeException("SHA-256 not supported", e);
        }
    }

    /**
     * 生成 6 字符短链后缀（Base62，取 SHA-256 摘要的高 35 位）
     */
    public String generateSuffix() {
        // 1. 生成 Snowflake 全局唯一 ID
        long rawId = snowflake.nextId();

        // 2. SHA-256 混淆
        byte[] idBytes = ByteBuffer.allocate(8).putLong(rawId).array();
        byte[] digest  = sha256.digest(idBytes);
        //得到 32 字节（256 位）的哈希值 digest

        // 3. 取前 35 位：digest[0..4] 共 40 位，右移 5 位保留高 35 位
        long bits40 = 0L;
        for (int i = 0; i < 5; i++) {
            bits40 = (bits40 << 8) | (digest[i] & 0xFF);
        }
        long bits35 = bits40 >>> 5;

        // 4. 分 6 段映射，每段 6 位，并对 62 取模，防止索引 62/63
        char[] suffix = new char[6];
        for (int i = 0; i < 6; i++) {
            int shift = 6 * (5 - i);
            int val   = (int)((bits35 >> shift) & 0x3F);  // 6 位值：0–63
            int idx   = val % BASE62.length;              // 映射到 0–61
            suffix[i] = BASE62[idx];
        }

        return new String(suffix);  // 固定 6 字符长度
    }
}
