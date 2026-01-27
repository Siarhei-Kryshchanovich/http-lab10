package com.example.http_lab10.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SuspiciousRequestTracker {

    private static final long WINDOW_SECONDS = 60;
    private static final int THRESHOLD = 10;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public boolean recordBadRequest(String ip) {
        Instant now = Instant.now();
        Bucket b = buckets.compute(ip, (k, v) -> {
            if (v == null || now.isAfter(v.windowStart.plusSeconds(WINDOW_SECONDS))) {
                return new Bucket(now, 1);
            }
            v.count++;
            return v;
        });
        return b.count >= THRESHOLD;
    }

    private static class Bucket {
        final Instant windowStart;
        int count;

        Bucket(Instant windowStart, int count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }
}
