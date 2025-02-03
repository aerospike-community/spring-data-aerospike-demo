package com.example.demo.simplecrudwithrestapi.configuration;

import com.example.demo.simplecrudwithrestapi.repository.UserRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractAerospikeDataConfiguration;
import org.springframework.data.aerospike.repository.config.EnableAerospikeRepositories;

@Configuration
@EnableAerospikeRepositories(basePackageClasses = UserRepository.class)
public class AerospikeConfiguration extends AbstractAerospikeDataConfiguration {

}

