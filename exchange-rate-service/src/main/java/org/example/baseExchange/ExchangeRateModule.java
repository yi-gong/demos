package org.example.baseExchange;

import com.google.inject.AbstractModule;
import org.example.baseExchange.service.ECBExchangeRateProvider;
import org.example.baseExchange.service.IExchangeRateProvider;
import org.example.baseExchange.service.cron.ExchangeRateJobFactory;
import org.example.baseExchange.service.grpc.client.ExchangeRatePushService;
import org.example.baseExchange.service.grpc.client.IExchangeRatePushService;
import org.example.baseExchange.service.grpc.server.ExchangeRatePullService;

public class ExchangeRateModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(IExchangeRatePushService.class).to(ExchangeRatePushService.class);
        bind(ExchangeRatePullService.class);
        bind(ExchangeRateJobFactory.class);
        bind(IExchangeRateProvider.class).to(ECBExchangeRateProvider.class);
        bind(ExchangeRateApplication.class);
    }
}
