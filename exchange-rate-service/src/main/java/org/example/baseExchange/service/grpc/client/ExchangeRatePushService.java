package org.example.baseExchange.service.grpc.client;

import com.example.core.ExchangeRatePush;
import com.google.inject.Singleton;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.core.ExchangePushServiceGrpc.ExchangePushServiceBlockingStub;
import static com.example.core.ExchangePushServiceGrpc.newBlockingStub;

@Singleton
public class ExchangeRatePushService implements IExchangeRatePushService {
    private static final Logger logger = LogManager.getLogger(ExchangeRatePushService.class);

    /**
     * Using DNS lookup parsing instances of service and prepare of broadcasting data.
     *
     * @return Key is GRPC blocking stub and value is push success flag.
     */
    private Map<ExchangePushServiceBlockingStub, Boolean> initStubManagedMap() {
        logger.debug("starting initialization of stubs");
        Map<ExchangePushServiceBlockingStub, Boolean> managedStubs = new ConcurrentHashMap<>();
        try {
            //TODO use configMap to inject service name and avoid logical circus dependency
            InetAddress[] allByName = InetAddress.getAllByName("currency-grpc-service");
            logger.debug("Get host addresses via DNS lookup " + allByName);
            Arrays.stream(allByName).parallel().forEach(inetAddress -> {
                initStubForAddress(managedStubs, inetAddress);
            });
        } catch (UnknownHostException e) {
            logger.error("Can not get ip from DNS lookup.", e);
        }
        return managedStubs;
    }

    /**
     * Establishing communication to all managed stubs, including 2 times retry if failure.
     * And initial the success flag of push event false for each stub.
     */
    private void initStubForAddress(Map<ExchangePushServiceBlockingStub, Boolean> managedStubs, InetAddress inetAddress) {
        String hostAddress = inetAddress.getHostAddress();
        logger.debug("Start establish connection to host address " + hostAddress);
        ManagedChannel managedChannel = null;
        int times = 3;
        while (times > 0) {
            managedChannel = initStub(hostAddress);
            if (managedChannel != null) {
                managedStubs.put(newBlockingStub(managedChannel), false);
                break;
            }
            times = times - 1;
            logger.debug("Establish connection to " + hostAddress + ", still " + times + " retry remains");
            // TODO add exponential Backoff
        }
        if (managedChannel == null) {
            // TODO Handle the error case if one instance can not be reached.
            logger.error("After 3 times, can not establish connection with host address" + hostAddress);
        }
    }

    /**
     * Initial the communication to target host address and ignore exception.
     *
     * @return Channel if communication successfully established, else NULL.
     */
    private ManagedChannel initStub(String hostAddress) {
        ManagedChannel managedChannel = null;
        try {
            managedChannel = NettyChannelBuilder.forAddress(hostAddress, 9090)
                    .usePlaintext().build();
        } catch (Exception e) {
            logger.error("Exception happens by establishing connection with host address " + hostAddress, e);
        }
        return managedChannel;
    }

    @Override
    public void pushExchangeMap(Map<String, String> map) {
        Map<ExchangePushServiceBlockingStub, Boolean> managedStubs = initStubManagedMap();
        managedStubs.entrySet().parallelStream().filter(entry -> !entry.getValue())
                .forEach(entry -> {
                    try {
                        ExchangeRatePush.ExchangePushResponse exchangePushResponse =
                                entry.getKey().pushExchangeRates(ExchangeRatePush.ExchangePushRequest.newBuilder().putAllExchangeRateMap(map).build());
                        if (ExchangeRatePush.ExchangePushResponse.Status.OK.equals(exchangePushResponse.getStatus())) {
                            managedStubs.put(entry.getKey(), true);
                        }
                    } catch (Exception e) {
                        // TODO Handle the error case if one instance can not be reached and retry push
                        logger.error("Error happens when pushing Data to rest service.", e);
                        logger.error("Pushing to other IPs still going on.", e);
                    }
                });
    }
}
