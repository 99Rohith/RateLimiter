package com.app.ratelimiter.service;

import com.app.ratelimiter.config.Constants;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

@Service
public class RateLimiterService {
    private final Map<Integer, Queue<LocalDateTime>> requests = new HashMap<>();

    public Long isRateLimited(Integer userId) {
        if (!requests.containsKey(userId)) {
            requests.put(userId, new LinkedList<>());
        }

        LocalDateTime now = LocalDateTime.now();
        while (!requests.get(userId).isEmpty()) {
            long diffSeconds = Duration.between(requests.get(userId).peek(), now).toSeconds();
            if (diffSeconds > Constants.RATE_LIMIT_TIMEOUT_SEC) {
                requests.get(userId).remove();
            } else {
                break;
            }
        }

        if (requests.get(userId).size() < Constants.RATE_LIMIT_MAX_REQUESTS) {
            requests.get(userId).add(now);
            return 0L;
        }

        return Math.abs(Constants.RATE_LIMIT_TIMEOUT_SEC -
                Duration.between(requests.get(userId).peek(), now).toSeconds());
    }
}
