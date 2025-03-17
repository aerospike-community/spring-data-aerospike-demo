package com.demo.reactive.index.configuration;

import com.aerospike.client.query.IndexType;
import com.demo.reactive.index.entity.IndexedMovieDocument;
import com.demo.reactive.index.repository.ReactiveIndexedDocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractReactiveAerospikeDataConfiguration;
import org.springframework.data.aerospike.core.ReactiveAerospikeTemplate;
import org.springframework.data.aerospike.repository.config.EnableReactiveAerospikeRepositories;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@EnableReactiveAerospikeRepositories(basePackageClasses = ReactiveIndexedDocumentRepository.class)
public class AerospikeConfiguration extends AbstractReactiveAerospikeDataConfiguration {

    private static final String INDEX_NAME = "reactive-movie-rating-index";

    @Bean
    @ConditionalOnProperty(
            value = "aerospike." + INDEX_NAME + ".create-on-startup",
            havingValue = "true",
            matchIfMissing = true)
    public Boolean createAerospikeIndex(ReactiveAerospikeTemplate reactiveTemplate) {
        return reactiveTemplate.createIndex(IndexedMovieDocument.class, INDEX_NAME, "rating", IndexType.NUMERIC)
                .then(Mono.fromCallable(() -> {
                    log.info("Index {} was successfully created", INDEX_NAME);
                    return true;
                })).block();
    }
}

