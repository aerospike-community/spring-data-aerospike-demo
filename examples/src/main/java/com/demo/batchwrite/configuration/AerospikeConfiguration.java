package com.demo.batchwrite.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractAerospikeDataConfiguration;
import org.springframework.data.aerospike.repository.config.EnableAerospikeRepositories;

@Configuration
@EnableAerospikeRepositories(basePackages = "com.demo.batchwrite.repository")
public class AerospikeConfiguration extends AbstractAerospikeDataConfiguration {

}
