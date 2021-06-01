package org.example.baseExchange.service.cron;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

/**
 * Quartz job factory to create new Job. Bind Quartz job creation with GUICE dependency Injection.
 */
@Singleton
public class ExchangeRateJobFactory implements JobFactory {

    @Inject
    private Provider<ExchangeRatePushJob> provider;

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) {
        return provider.get();
    }
}
