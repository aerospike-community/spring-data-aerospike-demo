package com.demo.reactive.batchread.repository;

import com.demo.reactive.batchread.entity.MovieDocumentForBatchRead;
import org.springframework.data.aerospike.repository.ReactiveAerospikeRepository;

public interface ReactiveMovieRepositoryForBatchRead extends ReactiveAerospikeRepository<MovieDocumentForBatchRead, String> {

}
