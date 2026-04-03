package com.demo.reactive.nettyeventloops.configuration;

import com.demo.reactive.nettyeventloops.repository.ReactiveMovieRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractReactiveAerospikeDataConfiguration;
import org.springframework.data.aerospike.repository.config.EnableReactiveAerospikeRepositories;

/**
 * Uses the default event loops provided by {@link
 * org.springframework.data.aerospike.config.AerospikeDataConfigurationSupport#eventLoops()}.
 * <p>
 * The default auto-selects the best available transport: Epoll on Linux, KQueue on macOS,
 * NIO as a fallback.
 */
@Configuration
@EnableReactiveAerospikeRepositories(basePackageClasses = ReactiveMovieRepository.class)
@ConditionalOnProperty(name = "demo.eventloop.type", havingValue = "default", matchIfMissing = true)
public class AerospikeConfigurationDefault extends AbstractReactiveAerospikeDataConfiguration {

}
