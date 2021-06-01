package org.example.baseExchange.service.cron;

import com.google.inject.Singleton;
import org.example.baseExchange.service.grpc.client.IExchangeRatePushService;
import org.example.baseExchange.service.IExchangeRateProvider;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import javax.inject.Inject;
import java.util.Map;

import static com.example.core.ExchangePushServiceGrpc.ExchangePushServiceBlockingStub;

/**
 * Quartz job, responsible for triggering communication establishment and push event.
 */
public class ExchangeRatePushJob implements Job {

    @Inject
    private IExchangeRatePushService exchangePushService;
    @Inject
    private IExchangeRateProvider exchangeProvider;


    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        Map<String, String> exchangeRate = exchangeProvider.provideExchangeRate();
        exchangePushService.pushExchangeMap(exchangeRate);
    }
}
