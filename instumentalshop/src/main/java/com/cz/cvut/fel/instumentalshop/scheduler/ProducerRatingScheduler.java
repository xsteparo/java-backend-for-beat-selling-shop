package com.cz.cvut.fel.instumentalshop.scheduler;

import com.cz.cvut.fel.instumentalshop.service.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProducerRatingScheduler {

    private final ProducerService producerService;

    @Scheduled(cron = "0 0 2 * * *") // каждый день в 2:00 ночи
    public void updateProducerRatings() {
        log.info("Starting scheduled producer ratings update...");

        try {
            producerService.updateProducerRatings();
            log.info("Producer ratings update completed successfully.");
        } catch (Exception e) {
            log.error("Failed to update producer ratings: {}", e.getMessage(), e);
        }
    }
}
