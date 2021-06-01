package org.example.baseExchange.service;

import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

/**
 * A demo external exchange provider, which should be replace by remote call to ECB to get real data.
 */
@Singleton
public class ECBExchangeRateProvider implements IExchangeRateProvider {
    private final Map<String, String> baseExchangeRate = new HashMap<>();

    @Override
    public Map<String, String> provideExchangeRate() {
        return getBaseExchangeRate();
    }

    private Map<String, String> getBaseExchangeRate() {
        if (baseExchangeRate.isEmpty()) {
            synchronized (this) {
                if (baseExchangeRate.isEmpty()) {
                    baseExchangeRate.put("USD", "1.2007");
                    baseExchangeRate.put("JPY", "129.80");
                    baseExchangeRate.put("BGN", "1.9558");
                    baseExchangeRate.put("CZK", "25893");
                    baseExchangeRate.put("DKK", "7.4364");
                    baseExchangeRate.put("GBP", "0.86250");
                    baseExchangeRate.put("EUR", "1.0");
                }
            }
        }
        return baseExchangeRate;
    }

}
