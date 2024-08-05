package com.click.batchServer.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class BatchScheduler {

    private final JobLauncher jobLauncher;

    private final Job importAccountHistoryJob;

    @Scheduled(cron = "0 */3 * * * ?")
    public void runJob() {
        try {
            jobLauncher.run(importAccountHistoryJob, new JobParameters());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
