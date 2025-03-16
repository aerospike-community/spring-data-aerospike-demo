package com.demo.reactive.batchread.configuration;

import com.demo.reactive.batchread.repository.ReactiveMovieRepositoryForBatchRead;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractReactiveAerospikeDataConfiguration;
import org.springframework.data.aerospike.config.AerospikeDataSettings;
import org.springframework.data.aerospike.repository.config.EnableReactiveAerospikeRepositories;

@Configuration
@EnableReactiveAerospikeRepositories(basePackageClasses = ReactiveMovieRepositoryForBatchRead.class)
public class AerospikeConfiguration extends AbstractReactiveAerospikeDataConfiguration {

    @Override
    protected void configureDataSettings(AerospikeDataSettings dataSettings) {
        dataSettings.setScansEnabled(true); // allow scan operations required for finding by entities (findAll)
    }
}
