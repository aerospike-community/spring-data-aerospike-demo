package com.demo.reactive.simplecrud.configuration;

import com.demo.reactive.simplecrud.repository.ReactiveMovieRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractReactiveAerospikeDataConfiguration;
import org.springframework.data.aerospike.repository.config.EnableReactiveAerospikeRepositories;

@Configuration
@EnableReactiveAerospikeRepositories(basePackageClasses = ReactiveMovieRepository.class)
public class AerospikeConfiguration extends AbstractReactiveAerospikeDataConfiguration {

}
