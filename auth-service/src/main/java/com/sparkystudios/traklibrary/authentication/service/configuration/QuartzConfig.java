package com.sparkystudios.traklibrary.authentication.service.configuration;

import com.sparkystudios.traklibrary.authentication.service.scheduling.jobs.RemoveExpiredRecoveryTokensJob;
import com.sparkystudios.traklibrary.authentication.service.scheduling.jobs.RemoveExpiredVerificationCodesJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail removeExpiredRecoveryTokensJobDetails() {
        return JobBuilder.newJob()
                .ofType(RemoveExpiredRecoveryTokensJob.class)
                .storeDurably()
                .withIdentity("remove-expired-recovery-tokens-job")
                .withDescription("Job to remove expired recovery tokens from auth_user table.")
                .build();
    }

    @Bean
    public Trigger removeExpiredRecoveryTokensTrigger(JobDetail removeExpiredRecoveryTokensJobDetails) {
        return TriggerBuilder.newTrigger()
                .forJob(removeExpiredRecoveryTokensJobDetails)
                .withIdentity("remove-expired-recovery-tokens-trigger")
                .withDescription("Trigger to remove expired recovery tokens from auth_user table.")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 * ? * *"))
                .build();
    }

    @Bean
    public JobDetail removeExpiredVerificationTokensJobDetails() {
        return JobBuilder.newJob()
                .ofType(RemoveExpiredVerificationCodesJob.class)
                .storeDurably()
                .withIdentity("remove-expired-verification-tokens-job")
                .withDescription("Job to remove expired verification tokens from auth_user table.")
                .build();
    }

    @Bean
    public Trigger removeExpiredVerificationTokensTrigger(JobDetail removeExpiredVerificationTokensJobDetails) {
        return TriggerBuilder.newTrigger()
                .forJob(removeExpiredVerificationTokensJobDetails)
                .withIdentity("remove-expired-verification-tokens-trigger")
                .withDescription("Trigger to remove verification recovery tokens from auth_user table.")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 30 * ? * *"))
                .build();
    }
}
