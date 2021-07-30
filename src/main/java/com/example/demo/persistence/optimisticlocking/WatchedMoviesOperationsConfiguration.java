package com.example.demo.persistence.optimisticlocking;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WatchedMoviesOperationsConfiguration {

    @Bean
    public WatchedMoviesOperations ageOperations(WatchedMoviesDocumentRepository repository) {
        return new WatchedMoviesOperations(repository);
    }
}
