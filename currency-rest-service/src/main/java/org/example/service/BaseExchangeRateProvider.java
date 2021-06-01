package org.example.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.service.grpc.client.BaseExchangeRatePullService;
import org.example.service.grpc.server.BaseExchangeRateCronPushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provide basic exchange rate EURO to other currencies, e.g. EUR to USD.
 * Implement listener to get notified once exchange rate received from exchange service.
 *
 * Provider will hold a local cache for quick get.
 * A request to remote service for base exchange rate will be fired when Bean initialization.
 *
 * When a push request from remote service comes, the cache will be replaced by new data.
 */
@Service
public class BaseExchangeRateProvider implements IDataReceiveListener {
    private static final Logger logger = LogManager.getLogger(BaseExchangeRateProvider.class);
    private Map<String, BigDecimal> baseExchangeRate = new ConcurrentHashMap<>();

    public BaseExchangeRateProvider(@Autowired BaseExchangeRatePullService client, @Autowired BaseExchangeRateCronPushService service) {
        client.registerListeners(this);
        service.registerListeners(this);
        client.requestDataSync();
    }

    /**
     * create and return a copy of base exchange rate map to avoid reference to the object.
     *
     * @return baseExchangeRate, key is currency and value is exchange rate in BigDecimal
     */
    public Map<String, BigDecimal> getBaseExchangeRate() {
        return new ConcurrentHashMap<>(baseExchangeRate);
    }

    @Override
    public void onDataReceived(Map<String, String> exchangeRateMap) {
        logger.debug("Received exchange data!" + exchangeRateMap);
        ConcurrentHashMap<String, BigDecimal> newMap = new ConcurrentHashMap<>();
        exchangeRateMap.forEach((k, v) -> newMap.put(k, new BigDecimal(v)));
        baseExchangeRate = newMap;
    }

    @Override
    public void onError(String errorMessage) {
        logger.error("Can not receive base exchange rate from cron service.");
        logger.error(errorMessage);
    }
}
