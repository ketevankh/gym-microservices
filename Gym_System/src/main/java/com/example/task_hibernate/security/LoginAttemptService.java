package com.example.task_hibernate.security;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {

    private final int MAX_ATTEMPT = 3;
    private final long BLOCK_TIME_MINUTES = 5;
    private ConcurrentMap<String, Attempt> attempts = new ConcurrentHashMap<>();

    public void loginSucceeded(String key) {
        attempts.remove(key);
    }

    public void loginFailed(String key) {
        Attempt attempt = attempts.get(key);
        if (attempt == null) {
            attempt = new Attempt();
            attempts.put(key, attempt);
        }
        attempt.increment();
    }

    public boolean isBlocked(String key) {
        Attempt attempt = attempts.get(key);
        return attempt != null && attempt.getAttempts() >= MAX_ATTEMPT &&
               TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - attempt.getLastAttempt()) < BLOCK_TIME_MINUTES;
    }

    private static final class Attempt {
        private int attempts;
        private long lastAttempt;

        void increment() {
            attempts++;
            lastAttempt = System.currentTimeMillis();
        }

        int getAttempts() {
            return attempts;
        }

        long getLastAttempt() {
            return lastAttempt;
        }
    }
}