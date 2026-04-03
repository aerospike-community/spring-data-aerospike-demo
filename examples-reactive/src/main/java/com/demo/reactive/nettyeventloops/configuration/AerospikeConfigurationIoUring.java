package com.demo.reactive.nettyeventloops.configuration;

import com.aerospike.client.async.EventLoops;
import com.aerospike.client.async.EventPolicy;
import com.aerospike.client.async.NettyEventLoops;
import com.demo.reactive.nettyeventloops.repository.ReactiveMovieRepository;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.uring.IoUringIoHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractReactiveAerospikeDataConfiguration;
import org.springframework.data.aerospike.repository.config.EnableReactiveAerospikeRepositories;

/**
 * Uses Linux io_uring via {@link IoUringIoHandler} for high-performance async I/O.
 * Requires Linux kernel 5.1+ with io_uring support and the
 * {@code netty-transport-native-io_uring} dependency on the classpath.
 */
@Configuration
@EnableReactiveAerospikeRepositories(basePackageClasses = ReactiveMovieRepository.class)
@ConditionalOnProperty(name = "demo.eventloop.type", havingValue = "iouring")
public class AerospikeConfigurationIoUring extends AbstractReactiveAerospikeDataConfiguration {

    @Override
    @Bean
    public EventLoops eventLoops() {
        int nThreads = Math.max(2, Runtime.getRuntime().availableProcessors() * 2);
        MultiThreadIoEventLoopGroup eventLoopGroup =
            new MultiThreadIoEventLoopGroup(nThreads, IoUringIoHandler.newFactory());

        EventPolicy eventPolicy = new EventPolicy();
        eventPolicy.maxCommandsInProcess = 40;
        eventPolicy.maxCommandsInQueue = 1024;
        return new NettyEventLoops(eventPolicy, eventLoopGroup);
    }
}
