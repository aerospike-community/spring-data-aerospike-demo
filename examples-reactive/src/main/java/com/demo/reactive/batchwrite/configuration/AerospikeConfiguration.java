package com.demo.reactive.batchwrite.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractReactiveAerospikeDataConfiguration;
import org.springframework.data.aerospike.repository.config.EnableReactiveAerospikeRepositories;

@Configuration
@EnableReactiveAerospikeRepositories(basePackages = "com.demo.reactive.batchwrite.repository")
public class AerospikeConfiguration extends AbstractReactiveAerospikeDataConfiguration {

}
