package org.example.baseExchange.service.grpc.server;

import com.example.core.ExchangePullServiceGrpc;
import com.google.inject.Singleton;
import io.grpc.stub.StreamObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.baseExchange.service.IExchangeRateProvider;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Map;

import static com.example.core.ExchangeRatePull.*;

/**
 * Service to receive pull request from client, asking for base exchange rate in map.
 * Give response with a map, key is the currency and value is the exchange rate in string.
 * <p>
 * GRPC server side
 */
@Singleton
public class ExchangeRatePullService extends ExchangePullServiceGrpc.ExchangePullServiceImplBase {
    private static final Logger logger = LogManager.getLogger(ExchangeRatePullService.class);

    @Inject
    private IExchangeRateProvider exchangeProvider;

    @Override
    public void pullExchangeRates(ExchangePullRequest request, StreamObserver<PullResponse> responseObserver) {
        try {
            logger.debug("Received request for exchange rate.");
            Map<String, String> baseExchangeRate = exchangeProvider.provideExchangeRate();
            long epochSecond = Instant.now().getEpochSecond();
            ExchangePullResponse exchangePullResponse
                    = ExchangePullResponse.newBuilder().putAllExchangeRateMap(baseExchangeRate).setTimestamp(epochSecond).build();
            PullResponse pullResponse = PullResponse.newBuilder().setExchange(exchangePullResponse).build();
            responseObserver.onNext(pullResponse);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("Error happened, no exchange data can be supplied for request, an error response returned to client.", e);
            responseObserver.onError(e);
        }

    }

}
