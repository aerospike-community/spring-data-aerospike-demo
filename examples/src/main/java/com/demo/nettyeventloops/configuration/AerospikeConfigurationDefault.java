package com.demo.nettyeventloops.configuration;

import com.demo.nettyeventloops.repository.MovieRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractAerospikeDataConfiguration;
import org.springframework.data.aerospike.repository.config.EnableAerospikeRepositories;

/**
 * Uses the default event loops provided by {@link
 * org.springframework.data.aerospike.config.AerospikeDataConfigurationSupport#eventLoops()}.
 * <p>
 * The default auto-selects the best available transport: Epoll on Linux, KQueue on macOS,
 * NIO as a fallback.
 */
@Configuration
@EnableAerospikeRepositories(basePackageClasses = MovieRepository.class)
@ConditionalOnProperty(name = "demo.eventloop.type", havingValue = "default", matchIfMissing = true)
public class AerospikeConfigurationDefault extends AbstractAerospikeDataConfiguration {

}
