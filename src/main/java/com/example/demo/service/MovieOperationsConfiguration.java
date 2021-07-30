package com.example.demo.service;

import com.example.demo.persistence.simplecrud.MovieRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.core.AerospikeTemplate;

@Configuration
public class MovieOperationsConfiguration {

    @Bean
    public MovieOperations movieOperations(MovieRepository repository,
                                           AerospikeTemplate template) {
        return new MovieOperations(repository, template);
    }
}
