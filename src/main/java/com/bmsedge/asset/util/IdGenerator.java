package com.bmsedge.asset.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {

    private static final String PREFIX = "AST";
    private static final AtomicInteger counter = new AtomicInteger(0);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * Generates a unique asset ID in the format: AST-YYYYMMDD-NNNN
     * Example: AST-20231215-0001
     */
    public static String generateAssetId() {
        String date = LocalDateTime.now().format(formatter);
        int count = counter.incrementAndGet();

        // Reset counter at 9999 to avoid overflow
        if (count >= 9999) {
            counter.set(0);
        }

        return String.format("%s-%s-%04d", PREFIX, date, count);
    }

    /**
     * Generates a simple UUID-based asset ID
     * Example: AST-a1b2c3d4-e5f6-7890-abcd-ef1234567890
     */
    public static String generateUuidBasedId() {
        return PREFIX + "-" + UUID.randomUUID().toString();
    }

    /**
     * Generates a short asset ID using timestamp and random number
     * Example: AST-1702650123-4567
     */
    public static String generateShortId() {
        long timestamp = System.currentTimeMillis() / 1000; // seconds
        int random = (int) (Math.random() * 9999);
        return String.format("%s-%d-%04d", PREFIX, timestamp, random);
    }
}