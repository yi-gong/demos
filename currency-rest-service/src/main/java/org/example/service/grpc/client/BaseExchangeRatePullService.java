package org.example.service.grpc.client;

import com.example.core.ExchangeRatePull;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.service.IDataReceiveListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.core.ExchangePullServiceGrpc.ExchangePullServiceBlockingStub;
import static com.example.core.ExchangePullServiceGrpc.newBlockingStub;

/**
 * Service to pull base exchange rate from exchange service and notify listeners if response come back.
 * Using GRPC blocking stub to establish connection, logically the client side of the pull service.
 */
@Service
public class BaseExchangeRatePullService {
    private final ExchangePullServiceBlockingStub stub;
    private static final Logger logger = LogManager.getLogger(BaseExchangeRatePullService.class);
    private final List<IDataReceiveListener> listeners = new ArrayList<>();

    public BaseExchangeRatePullService() {
        ManagedChannel managedChannel = NettyChannelBuilder.forAddress("exchange-rate-service", 8901)
                .usePlaintext().build();
        stub = newBlockingStub(managedChannel);
    }

    /**
     * request for exchange rate, retry mechanism need to be added.
     */
    public void requestDataSync() {
        logger.debug("Start pull exchange rate.");
        try {
            ExchangeRatePull.PullResponse exchangeResponse =
                    stub.pullExchangeRates(ExchangeRatePull.ExchangePullRequest.newBuilder().build());
            switch (exchangeResponse.getResponseCase()) {
                case EXCHANGE:
                    logger.debug("Received exchange from server.");
                    Map<String, String> exchangeRateMap = exchangeResponse.getExchange().getExchangeRateMapMap();
                    listeners.forEach(l -> l.onDataReceived(exchangeRateMap));
                case ERROR:
                    logger.debug("Received error from server.");
                    ExchangeRatePull.ErrorResponse responseError = exchangeResponse.getError();
                    listeners.forEach(l -> l.onError(responseError.getErrorMessage()));
            }
        } catch (Exception e) {
            logger.error("Error happens retrieve data from GRPC server ", e);
            listeners.forEach(l -> l.onError(e.getMessage()));
        }
    }

    /**
     * register listener to be notified
     *
     * @param listener the listener
     */
    public void registerListeners(IDataReceiveListener listener) {
        listeners.add(listener);
    }
}
