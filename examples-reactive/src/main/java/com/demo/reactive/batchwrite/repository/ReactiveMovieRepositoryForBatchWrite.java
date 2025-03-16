package com.demo.reactive.batchwrite.repository;

import com.demo.reactive.batchwrite.entity.MovieDocumentForBatchWrite;
import org.springframework.data.aerospike.repository.ReactiveAerospikeRepository;

public interface ReactiveMovieRepositoryForBatchWrite extends ReactiveAerospikeRepository<MovieDocumentForBatchWrite, String> {

}
