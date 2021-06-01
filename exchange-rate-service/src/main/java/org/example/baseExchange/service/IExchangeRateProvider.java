package org.example.baseExchange.service;

import java.util.Map;

/**
 * provide exchange rate
 */
public interface IExchangeRateProvider {
    /**
     * provide base exchange rate, the base currency is EURO, the exchange rate is EURO to other currency, e.g. EUR/USD
     *
     * @return a map, key is the currency and value is the exchange rate in string
     */
    Map<String, String> provideExchangeRate();
}
