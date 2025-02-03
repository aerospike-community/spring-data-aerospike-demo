package com.example.demo.simplecrud.configuration;

import com.example.demo.simplecrud.repository.MovieRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractAerospikeDataConfiguration;
import org.springframework.data.aerospike.repository.config.EnableAerospikeRepositories;

@Configuration
@EnableAerospikeRepositories(basePackageClasses = MovieRepository.class)
public class AerospikeConfiguration extends AbstractAerospikeDataConfiguration {

}

