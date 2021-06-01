package org.example.service;

import com.example.core.ExchangeRatePush;
import io.grpc.stub.StreamObserver;
import org.example.service.grpc.client.BaseExchangeRatePullService;
import org.example.service.grpc.server.BaseExchangeRateCronPushService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.Map;


@SpringBootTest
@TestPropertySource(locations = "classpath:testApplication.properties")
public class BaseExchangeRateProviderPullTest extends CurrencyTestBase {
    @Autowired
    private BaseExchangeRateProvider baseExchangeRateProvider;

    @TestConfiguration
    static class BaseExchangeRateProviderITestConfiguration extends CurrencyTestBase {
        @Bean
        public BaseExchangeRatePullService baseExchangeRatePullService() {
            return new BaseExchangeRatePullService() {
                IDataReceiveListener listener;

                @Override
                public void registerListeners(IDataReceiveListener listener) {
                    this.listener = listener;
                }

                @Override
                public void requestDataSync() {
                    listener.onDataReceived(getTestExchangeRatesString());
                }
            };
        }
    }

    @Test
    public void pull_dataInitialization_test() {
        Map<String, BigDecimal> baseExchangeRate = baseExchangeRateProvider.getBaseExchangeRate();
        Assertions.assertFalse(baseExchangeRate.isEmpty());
        Assertions.assertNotNull(baseExchangeRate.get("EUR"));
    }
}
