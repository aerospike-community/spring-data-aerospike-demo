package com.example.demo.persistence.index;

import com.aerospike.client.query.IndexType;
import com.example.demo.persistence.simplecrud.MovieDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.core.AerospikeTemplate;

@Slf4j
@Configuration
public class AerospikeIndexConfiguration {

    private static final String INDEX_NAME = "movie-rating-index";

    @Bean
    @ConditionalOnProperty(
            value = "aerospike." + INDEX_NAME + ".create-on-startup",
            havingValue = "true",
            matchIfMissing = true)
    public boolean createAerospikeIndex(AerospikeTemplate aerospikeTemplate) {
        aerospikeTemplate.createIndex(MovieDocument.class, INDEX_NAME, "rating", IndexType.NUMERIC);
        log.info("Index {} was successfully created", INDEX_NAME);
        return true;
    }
}
