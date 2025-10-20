package com.bryan.daft_pinger;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bryan.daft_pinger.jobs.DaftPingerJob;

@Configuration
public class QuartzConfig {


    @Value("${daft.pinger.job.timer}")
    private int jobTimerInMinutes;

    @Bean
    public JobDetail daftWatcherJobDetail() {
        return JobBuilder.newJob(DaftPingerJob.class)
                .withIdentity("daftWatcherJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger daftWatcherTrigger(JobDetail daftWatcherJobDetail) {
        // Every 15 minutes
        return TriggerBuilder.newTrigger()
                .forJob(daftWatcherJobDetail)
                .withIdentity("daftWatcherTrigger")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMinutes(jobTimerInMinutes)
                        .repeatForever())
                .build();
    }
}
