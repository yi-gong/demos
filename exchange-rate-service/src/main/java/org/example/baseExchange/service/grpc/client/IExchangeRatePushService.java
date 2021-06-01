package org.example.baseExchange.service.grpc.client;

import com.example.core.ExchangePushServiceGrpc;

import java.util.Map;

/**
 * Service to push base exchange rate and retry if connection can not be established or error happens.
 * Using GRPC blocking stub to establish connection, logically the client side of the push service.
 */
public interface IExchangeRatePushService {
    /**
     * Pushing data to endpoints.
     *
     * @param map the exchange rate in map to push
     */
    void pushExchangeMap(Map<String, String> map);
}
