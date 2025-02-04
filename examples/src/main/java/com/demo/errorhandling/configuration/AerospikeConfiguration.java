package com.demo.errorhandling.configuration;

import com.demo.errorhandling.repository.MovieRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractAerospikeDataConfiguration;
import org.springframework.data.aerospike.repository.config.EnableAerospikeRepositories;

@Configuration
@EnableAerospikeRepositories(basePackageClasses = MovieRepository.class)
public class AerospikeConfiguration extends AbstractAerospikeDataConfiguration {

}