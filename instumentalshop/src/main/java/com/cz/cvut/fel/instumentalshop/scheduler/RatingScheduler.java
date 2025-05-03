package com.cz.cvut.fel.instumentalshop.scheduler;

import com.cz.cvut.fel.instumentalshop.service.RatingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RatingScheduler {

    private final RatingService ratingService;

    public RatingScheduler(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @Scheduled(cron = "0 0 2 * * *")  // каждый день в 2:00
    public void scheduledUpdateRatings() {
        System.out.println("Starting daily rating update...");
        ratingService.updateAllRatings();
        System.out.println("Daily rating update completed.");
    }
}
