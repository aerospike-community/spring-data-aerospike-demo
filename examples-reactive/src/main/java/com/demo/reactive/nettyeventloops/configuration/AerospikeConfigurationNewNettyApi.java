package com.demo.reactive.nettyeventloops.configuration;

import com.aerospike.client.async.EventLoops;
import com.aerospike.client.async.EventPolicy;
import com.aerospike.client.async.NettyEventLoops;
import com.demo.reactive.nettyeventloops.repository.ReactiveMovieRepository;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollIoHandler;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueIoHandler;
import io.netty.channel.nio.NioIoHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractReactiveAerospikeDataConfiguration;
import org.springframework.data.aerospike.repository.config.EnableReactiveAerospikeRepositories;

import java.util.Locale;

/**
 * Uses the Netty 4.2 {@link MultiThreadIoEventLoopGroup} API with {@code IoHandler} factories
 * instead of the legacy {@code EpollEventLoopGroup}/{@code NioEventLoopGroup} classes.
 * <p>
 * The event loop type is auto-detected by {@link NettyEventLoops} from the group instance.
 */
@Configuration
@EnableReactiveAerospikeRepositories(basePackageClasses = ReactiveMovieRepository.class)
@ConditionalOnProperty(name = "demo.eventloop.type", havingValue = "new-netty-api")
public class AerospikeConfigurationNewNettyApi extends AbstractReactiveAerospikeDataConfiguration {

    @Override
    @Bean
    public EventLoops eventLoops() {
        int nThreads = Math.max(2, Runtime.getRuntime().availableProcessors() * 2);
        String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);

        EventLoopGroup eventLoopGroup;
        if (os.contains("nux") && Epoll.isAvailable()) {
            eventLoopGroup = new MultiThreadIoEventLoopGroup(nThreads, EpollIoHandler.newFactory());
        } else if (os.contains("mac") && KQueue.isAvailable()) {
            eventLoopGroup = new MultiThreadIoEventLoopGroup(nThreads, KQueueIoHandler.newFactory());
        } else {
            eventLoopGroup = new MultiThreadIoEventLoopGroup(nThreads, NioIoHandler.newFactory());
        }

        EventPolicy eventPolicy = new EventPolicy();
        eventPolicy.maxCommandsInProcess = 40;
        eventPolicy.maxCommandsInQueue = 1024;
        return new NettyEventLoops(eventPolicy, eventLoopGroup);
    }
}
