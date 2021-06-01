package org.example.baseExchange;

import com.example.core.ExchangePullServiceGrpc;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import org.example.baseExchange.service.cron.ExchangeRateJobFactory;
import org.example.baseExchange.service.cron.ExchangeRatePushJob;
import org.example.baseExchange.service.grpc.server.ExchangeRatePullService;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Single instance application, which schedule a cron job to broadcasting newly fetched data to other services.
 */
@Singleton
public class ExchangeRateApplication {

    public static void main(String[] args) throws IOException, InterruptedException, SchedulerException {
        Injector injector = Guice.createInjector(new ExchangeRateModule());
        ExchangeRateApplication application = injector.getInstance(ExchangeRateApplication.class);
        application.start();
    }

    @Inject
    private ExchangeRateJobFactory jobFactory;

    @Inject
    private ExchangeRatePullService exchangeRatePullService;


    private void start() throws SchedulerException, IOException, InterruptedException {
        scheduleJob();
        NettyServerBuilder nettyServerBuilder = NettyServerBuilder.forAddress(new InetSocketAddress("0.0.0.0", 8901))
                .addService(exchangeRatePullService);
        Server server = nettyServerBuilder.build();
        server.start();
        server.awaitTermination();

    }


    /**
     * Schedule a Cron based event
     *
     * @throws SchedulerException if scheduling of cron based event failed.
     */
    private void scheduleJob() throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(ExchangeRatePushJob.class).withIdentity("pushJob").build();
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("pushJobTrigger").withSchedule(CronScheduleBuilder.cronSchedule("0 0 1 ? * * *")).forJob("pushJob").build();
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.setJobFactory(jobFactory);
        scheduler.scheduleJob(jobDetail, trigger);
        scheduler.start();
    }
}


