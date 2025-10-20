package com.bryan.daft_pinger.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import com.bryan.daft_pinger.service.DaftScraperHeadlessService;
import com.bryan.daft_pinger.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DaftPingerJob implements Job{

    private final DaftScraperHeadlessService scraperHeadlessService;

    private final NotificationService notificationService;

    @Override
    public void execute(JobExecutionContext context) {
        log.info("Running Daft Pinger job...");
        try {
            var newListings = scraperHeadlessService.checkForNewListings();
            for (var listing : newListings) {
                notificationService.send("New property: " + listing);
            }
        } catch (Exception e) {
            log.error("Error checking Daft listings", e);
        }
    }
}
