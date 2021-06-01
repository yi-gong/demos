package org.example.service.grpc.server;

import com.example.core.ExchangePushServiceGrpc;
import com.example.core.ExchangeRatePush;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.service.IDataReceiveListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service to receive push data from exchange service once the exchange rate changes.
 * Listeners will be notified after data received.
 * Give response with status 'OK' back to exchange service if success.
 * <p>
 * GRPC server side, prepare endpoint getting data pushed from other service.
 */
@GrpcService
public class BaseExchangeRateCronPushService extends ExchangePushServiceGrpc.ExchangePushServiceImplBase {

    private final List<IDataReceiveListener> listeners = new ArrayList<>();

    @Override
    public void pushExchangeRates(ExchangeRatePush.ExchangePushRequest request, StreamObserver<ExchangeRatePush.ExchangePushResponse> responseObserver) {
        Map<String, String> exchangeRateMap = request.getExchangeRateMapMap();
        listeners.forEach(l -> l.onDataReceived(exchangeRateMap));
        ExchangeRatePush.ExchangePushResponse response = ExchangeRatePush.ExchangePushResponse.newBuilder()
                .setStatus(ExchangeRatePush.ExchangePushResponse.Status.OK).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * register listener to be notified if pushed data arrived
     *
     * @param listener the listener
     */
    public void registerListeners(IDataReceiveListener listener) {
        listeners.add(listener);
    }
}
