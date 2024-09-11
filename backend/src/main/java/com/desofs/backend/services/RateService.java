package com.desofs.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class RateService {

    private final ConcurrentHashMap<String, Integer> userRates = new ConcurrentHashMap<>();

    public int getUserRate(String user) {
        return userRates.getOrDefault(user, 0);
    }

    public void increaseRate(String user) {
        userRates.merge(user, 1, Integer::sum);
    }

    @Scheduled(cron = "* * 3 * * *")
    private void clearRates() {
        userRates.clear();
    }
}
