package com.demo.reactive.nettyeventloops.configuration;

import com.aerospike.client.async.EventLoopType;
import com.aerospike.client.async.EventLoops;
import com.aerospike.client.async.EventPolicy;
import com.aerospike.client.async.NettyEventLoops;
import com.demo.reactive.nettyeventloops.repository.ReactiveMovieRepository;
import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractReactiveAerospikeDataConfiguration;
import org.springframework.data.aerospike.repository.config.EnableReactiveAerospikeRepositories;

/**
 * Overrides the default event loops with a custom {@link NioEventLoopGroup} configuration:
 * fixed thread count and tuned {@link EventPolicy} values.
 */
@Configuration
@EnableReactiveAerospikeRepositories(basePackageClasses = ReactiveMovieRepository.class)
@ConditionalOnProperty(name = "demo.eventloop.type", havingValue = "custom-nio")
public class AerospikeConfigurationCustomNio extends AbstractReactiveAerospikeDataConfiguration {

    @Override
    @Bean
    public EventLoops eventLoops() {
        int nThreads = 4;
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup(nThreads);

        EventPolicy eventPolicy = new EventPolicy();
        eventPolicy.maxCommandsInProcess = 100;
        eventPolicy.maxCommandsInQueue = 2048;
        return new NettyEventLoops(eventPolicy, eventLoopGroup, EventLoopType.NETTY_NIO);
    }
}
