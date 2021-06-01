package org.example.service;

import java.util.Map;

/**
 * Listener to get notified if data received from exchange rate service.
 */
public interface IDataReceiveListener {
    /**
     * Method called once data received
     *
     * @param exchangeRateMap the exchange rate map, key is currency, value is exchange rate.
     */
    void onDataReceived(Map<String, String> exchangeRateMap);

    /**
     * Method called once error happened
     *
     * @param errorMessage the error message
     */
    void onError(String errorMessage);
}
