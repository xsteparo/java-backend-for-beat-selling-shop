package com.cz.cvut.fel.instumentalshop.scheduler;

import com.cz.cvut.fel.instumentalshop.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RatingScheduler {

    private final RatingService ratingService;

    @Scheduled(cron = "0 0 2 * * *")
    public void scheduledUpdateRatings() {
        log.info("Starting daily rating update...");

        try {
            ratingService.updateAllRatings();
            log.info("Daily rating update completed...");
        } catch (Exception e) {
            log.error("Failed to update ratings: {}", e.getMessage(), e);
        }
    }
}
