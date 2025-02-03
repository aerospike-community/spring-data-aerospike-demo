package com.example.demo.optimisticlocking.configuration;

import com.example.demo.optimisticlocking.repository.WatchedMoviesDocumentRepository;
import com.example.demo.optimisticlocking.service.WatchedMoviesOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WatchedMoviesOperationsConfiguration {

    @Bean
    public WatchedMoviesOperations ageOperations(WatchedMoviesDocumentRepository repository) {
        return new WatchedMoviesOperations(repository);
    }
}
