package org.example.service;

import org.example.exception.CurrencyUnsupportedException;
import org.example.exception.ServiceUnpreparedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Exchange rate service to calculate exchange rate of currencies based on base exchange rate (EUR TO XXX)
 */
@Service
public class ExchangeRateService {

    @Autowired
    private final BaseExchangeRateProvider baseExchangeRateProvider;

    /**
     * @return supported currencies in list
     */
    public List<String> getSupportedCurrencies() {
        Map<String, BigDecimal> currencies = getCurrencies();
        return new ArrayList<>(currencies.keySet());
    }


    /**
     * Get exchange rate of the source currency base on Euro
     *
     * @param source the currency requested
     * @return exchange rate EURO to the source currency, NULL if no values can be get from cron backend service
     */
    public BigDecimal getEuroTo(String source) {
        Map<String, BigDecimal> currencies = getCurrencies();
        if (isCurrencySupported(source, currencies.keySet())) {
            String sourceUpper = source.toUpperCase();
            return currencies.get(sourceUpper);
        } else {
            return null;
        }
    }

    /**
     * calculate exchange rate of two currencies base on EUR/X
     * 5 digits will be remain and rounding mode is Half_even
     * e.g. HUF/USD
     *
     * @param source is HUF
     * @param target is the USD
     * @return the exchange rate of source/target, e.g. HUF/USD
     */
    public BigDecimal calculateExchangeRate(String source, String target) {
        Map<String, BigDecimal> currencies = getCurrencies();
        if (isCurrencySupported(source, currencies.keySet()) && isCurrencySupported(target, currencies.keySet())) {
            String sourceUpper = source.toUpperCase();
            BigDecimal euroToSource = currencies.get(sourceUpper);
            String targetUpper = target.toUpperCase();
            BigDecimal euroToTarget = currencies.get(targetUpper);
            return euroToTarget.divide(euroToSource, 4, RoundingMode.HALF_EVEN);
        } else return null;
    }

    /**
     * Check if supported currency requested
     *
     * @param currency            the requested currency
     * @param supportedCurrencies supported currencies
     * @return true if supported
     */
    boolean isCurrencySupported(String currency, Set<String> supportedCurrencies) {
        if (currency != null && !currency.isEmpty() &&
                supportedCurrencies.contains(currency.toUpperCase())) {
            return true;
        } else throw new CurrencyUnsupportedException();
    }

    /**
     * Check liveness of this application, if cache of base exchange rate fulfilled.
     * No exception thrown if service not really
     *
     * @return true if service ready, false otherwise.
     */
    public boolean readyForUse() {
        return !getSupportedCurrencies().isEmpty();
    }

    /**
     * Get cached base exchange rate, if empty exception will be thrown to give client error response.
     *
     * @return currencies from cache
     */
    private Map<String, BigDecimal> getCurrencies() {
        Map<String, BigDecimal> currencies = baseExchangeRateProvider.getBaseExchangeRate();
        if (currencies.isEmpty()) {
            throw new ServiceUnpreparedException();
        }
        return currencies;
    }

    /**
     * For Unit test only
     */
    ExchangeRateService(BaseExchangeRateProvider baseExchangeRateProvider) {
        this.baseExchangeRateProvider = baseExchangeRateProvider;
    }
}
