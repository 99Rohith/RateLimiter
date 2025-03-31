package com.app.ratelimiter.controller;

import com.app.ratelimiter.service.RateLimiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RateLimitController {
    @Autowired
    private final RateLimiterService mRateLimiterService;

    public RateLimitController(RateLimiterService mRateLimiterService) {
        this.mRateLimiterService = mRateLimiterService;
    }

    @GetMapping
    public ResponseEntity<String> isWorking() {
        return new ResponseEntity<>("Welcome to the Rate Limiter!", HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<String> isRateLimited(@PathVariable String userId) {
        Long rateLimitedSec = 0L;
        try {
            rateLimitedSec = mRateLimiterService.isRateLimited(Integer.parseInt(userId));
        } catch (NumberFormatException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (rateLimitedSec != 0L) {
            return new ResponseEntity<>("Request rate limited, try after " + rateLimitedSec + " seconds",
                    HttpStatus.TOO_MANY_REQUESTS);
        } else {
            return new ResponseEntity<>("Request passed through", HttpStatus.OK);
        }
    }
}
