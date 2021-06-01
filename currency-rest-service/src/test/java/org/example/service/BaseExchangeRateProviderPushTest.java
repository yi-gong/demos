package org.example.service;

import com.example.core.ExchangeRatePush;
import io.grpc.stub.StreamObserver;
import org.example.service.grpc.server.BaseExchangeRateCronPushService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.Map;


@SpringBootTest
@TestPropertySource(locations = "classpath:testApplication.properties")
public class BaseExchangeRateProviderPushTest extends CurrencyTestBase {
    @Autowired
    private BaseExchangeRateProvider baseExchangeRateProvider;
    @Autowired
    BaseExchangeRateProviderITestConfiguration.TestPusher testPusher;
    @TestConfiguration
    static class BaseExchangeRateProviderITestConfiguration extends CurrencyTestBase {
        @Bean
        public BaseExchangeRateCronPushService baseExchangeRateCronPushService() {
            return new BaseExchangeRateCronPushService() {
                IDataReceiveListener listener;

                @Override
                public void registerListeners(IDataReceiveListener listener) {
                    this.listener = listener;
                }

                @Override
                public void pushExchangeRates(ExchangeRatePush.ExchangePushRequest request, StreamObserver<ExchangeRatePush.ExchangePushResponse> responseObserver) {
                    listener.onDataReceived(getTestExchangeRatesString());
                }
            };
        }

        @Service
        public class TestPusher {
            private BaseExchangeRateCronPushService pushService;

            public TestPusher(@Autowired BaseExchangeRateCronPushService pushService) {
                this.pushService = pushService;
            }

            public void push() {
                pushService.pushExchangeRates(null, null);
            }
        }

    }

    @Test
    public void push_dataInitialization_test() {
        testPusher.push();
        Map<String, BigDecimal> baseExchangeRate = baseExchangeRateProvider.getBaseExchangeRate();
        Assertions.assertFalse(baseExchangeRate.isEmpty());
        Assertions.assertNotNull(baseExchangeRate.get("EUR"));
    }
}
