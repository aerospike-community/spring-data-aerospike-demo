package com.demo.batchread.configuration;

import com.demo.batchread.repository.MovieRepositoryForBatchRead;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.aerospike.config.AbstractAerospikeDataConfiguration;
import org.springframework.data.aerospike.config.AerospikeDataSettings;
import org.springframework.data.aerospike.repository.config.EnableAerospikeRepositories;

@Configuration
@EnableAerospikeRepositories(basePackageClasses = MovieRepositoryForBatchRead.class)
public class AerospikeConfiguration extends AbstractAerospikeDataConfiguration {

    @Override
    protected void configureDataSettings(AerospikeDataSettings dataSettings) {
        dataSettings.setScansEnabled(true); // allow scan operations required for finding by entities (findAll)
    }
}
