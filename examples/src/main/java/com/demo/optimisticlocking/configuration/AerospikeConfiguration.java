package com.demo.optimisticlocking.configuration;

import com.demo.optimisticlocking.repository.WatchedMoviesDocumentRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractAerospikeDataConfiguration;
import org.springframework.data.aerospike.repository.config.EnableAerospikeRepositories;

@Configuration
@EnableAerospikeRepositories(basePackageClasses = WatchedMoviesDocumentRepository.class)
public class AerospikeConfiguration extends AbstractAerospikeDataConfiguration {

}