package com.demo.reactive.batchwrite.repository;

import com.demo.reactive.batchwrite.entity.MovieDocumentForBatchWriteVersioned;
import org.springframework.data.aerospike.repository.ReactiveAerospikeRepository;

public interface ReactiveMovieRepositoryForBatchWriteVersioned
        extends ReactiveAerospikeRepository<MovieDocumentForBatchWriteVersioned, String> {

}
