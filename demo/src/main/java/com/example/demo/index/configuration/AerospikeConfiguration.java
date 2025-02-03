package com.example.demo.index.configuration;

import com.aerospike.client.query.IndexType;
import com.example.demo.index.entity.IndexedMovieDocument;
import com.example.demo.index.repository.IndexedDocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractAerospikeDataConfiguration;
import org.springframework.data.aerospike.core.AerospikeTemplate;
import org.springframework.data.aerospike.repository.config.EnableAerospikeRepositories;

@Slf4j
@Configuration
@EnableAerospikeRepositories(basePackageClasses = IndexedDocumentRepository.class)
public class AerospikeConfiguration extends AbstractAerospikeDataConfiguration {

    private static final String INDEX_NAME = "movie-rating-index";

    @Bean
    @ConditionalOnProperty(
            value = "aerospike." + INDEX_NAME + ".create-on-startup",
            havingValue = "true",
            matchIfMissing = true)
    public boolean createAerospikeIndex(AerospikeTemplate aerospikeTemplate) {
        aerospikeTemplate.createIndex(IndexedMovieDocument.class, INDEX_NAME, "rating", IndexType.NUMERIC);
        log.info("Index {} was successfully created", INDEX_NAME);
        return true;
    }
}

