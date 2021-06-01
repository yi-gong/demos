package org.example.service;

import java.math.BigDecimal;
import java.util.*;

public class CurrencyTestBase {
    private static final Map<String, BigDecimal> baseExchangeRate = new HashMap<>();

    static {
        baseExchangeRate.put("USD", BigDecimal.valueOf(1.258));
        baseExchangeRate.put("JPY", BigDecimal.valueOf(136.25));
        baseExchangeRate.put("BGN", BigDecimal.valueOf(1.688));
        baseExchangeRate.put("CZK", BigDecimal.valueOf(69854));
        baseExchangeRate.put("DKK", BigDecimal.valueOf(7.4364));
        baseExchangeRate.put("GBP", BigDecimal.valueOf(0.86250));
        baseExchangeRate.put("EUR", BigDecimal.valueOf(1.0));
    }

    protected Map<String, BigDecimal> getTestExchangeRatesBigDecimal() {
        return baseExchangeRate;
    }

    protected Map<String, String> getTestExchangeRatesString() {
        Map<String, String> baseExchangeRate = new HashMap<>();
        getTestExchangeRatesBigDecimal().entrySet().forEach((e ->
                baseExchangeRate.put(e.getKey(), e.getValue().toString())));
        return baseExchangeRate;
    }

    protected Set<String> getSupportedCurrency() {
        return baseExchangeRate.keySet();
    }

    protected List<String> getCurrenciesRequested() {
        return new ArrayList<>(baseExchangeRate.keySet());
    }
}
