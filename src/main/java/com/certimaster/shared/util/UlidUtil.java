package com.certimaster.shared.util;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Pure-Java ULID generator.
 * Format: 26 Crockford Base32 characters (10 timestamp + 16 random).
 * Encoding: 48-bit millisecond timestamp + 80-bit random.
 */
public final class UlidUtil {

    private static final char[] ENCODING = "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toCharArray();

    private UlidUtil() {
    }

    /**
     * Generate a new ULID string.
     */
    public static String generate() {
        long tsMs = Instant.now().toEpochMilli();
        long randHi = ThreadLocalRandom.current().nextLong();
        long randLo = ThreadLocalRandom.current().nextLong();
        return encode(tsMs, randHi, randLo);
    }

    private static String encode(long tsMs, long randHi, long randLo) {
        char[] ulid = new char[26];

        // 10 chars for 48-bit timestamp (most significant first)
        ulid[0] = ENCODING[(int) ((tsMs >>> 45) & 0x1F)];
        ulid[1] = ENCODING[(int) ((tsMs >>> 40) & 0x1F)];
        ulid[2] = ENCODING[(int) ((tsMs >>> 35) & 0x1F)];
        ulid[3] = ENCODING[(int) ((tsMs >>> 30) & 0x1F)];
        ulid[4] = ENCODING[(int) ((tsMs >>> 25) & 0x1F)];
        ulid[5] = ENCODING[(int) ((tsMs >>> 20) & 0x1F)];
        ulid[6] = ENCODING[(int) ((tsMs >>> 15) & 0x1F)];
        ulid[7] = ENCODING[(int) ((tsMs >>> 10) & 0x1F)];
        ulid[8] = ENCODING[(int) ((tsMs >>> 5) & 0x1F)];
        ulid[9] = ENCODING[(int) (tsMs & 0x1F)];

        // 16 chars for 80-bit randomness (hi 40 bits + lo 40 bits)
        ulid[10] = ENCODING[(int) ((randHi >>> 35) & 0x1F)];
        ulid[11] = ENCODING[(int) ((randHi >>> 30) & 0x1F)];
        ulid[12] = ENCODING[(int) ((randHi >>> 25) & 0x1F)];
        ulid[13] = ENCODING[(int) ((randHi >>> 20) & 0x1F)];
        ulid[14] = ENCODING[(int) ((randHi >>> 15) & 0x1F)];
        ulid[15] = ENCODING[(int) ((randHi >>> 10) & 0x1F)];
        ulid[16] = ENCODING[(int) ((randHi >>> 5) & 0x1F)];
        ulid[17] = ENCODING[(int) (randHi & 0x1F)];
        ulid[18] = ENCODING[(int) ((randLo >>> 35) & 0x1F)];
        ulid[19] = ENCODING[(int) ((randLo >>> 30) & 0x1F)];
        ulid[20] = ENCODING[(int) ((randLo >>> 25) & 0x1F)];
        ulid[21] = ENCODING[(int) ((randLo >>> 20) & 0x1F)];
        ulid[22] = ENCODING[(int) ((randLo >>> 15) & 0x1F)];
        ulid[23] = ENCODING[(int) ((randLo >>> 10) & 0x1F)];
        ulid[24] = ENCODING[(int) ((randLo >>> 5) & 0x1F)];
        ulid[25] = ENCODING[(int) (randLo & 0x1F)];

        return new String(ulid);
    }

    /**
     * Validate ULID format: 26 Crockford Base32 characters.
     */
    public static boolean isValid(String ulid) {
        if (ulid == null || ulid.length() != 26) {
            return false;
        }
        String upper = ulid.toUpperCase();
        for (char c : upper.toCharArray()) {
            if (new String(ENCODING).indexOf(c) < 0) {
                return false;
            }
        }
        return true;
    }
}
