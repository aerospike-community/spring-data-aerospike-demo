package com.demo.simplecrudrest.configuration;

import com.demo.simplecrudrest.repository.UserRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractAerospikeDataConfiguration;
import org.springframework.data.aerospike.repository.config.EnableAerospikeRepositories;

@Configuration
@EnableAerospikeRepositories(basePackageClasses = UserRepository.class)
public class AerospikeConfiguration extends AbstractAerospikeDataConfiguration {

}
