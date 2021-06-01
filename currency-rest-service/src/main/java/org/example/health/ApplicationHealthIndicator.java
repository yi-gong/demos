package org.example.health;

import org.example.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Use this indicator together with K8S readiness and liveness to avoid request going to unprepared instances.
 */
@Component
public class ApplicationHealthIndicator implements HealthIndicator {
    @Autowired
    public ExchangeRateService exchangeRateService;


    @Override
    public Health health() {
        if (exchangeRateService.readyForUse()) {
            return Health.up().build();
        } else return Health.down().build();
    }
}
