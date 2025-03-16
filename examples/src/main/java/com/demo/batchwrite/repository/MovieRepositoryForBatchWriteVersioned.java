package com.demo.batchwrite.repository;

import com.demo.batchwrite.entity.MovieDocumentForBatchWriteVersioned;
import org.springframework.data.aerospike.repository.AerospikeRepository;

public interface MovieRepositoryForBatchWriteVersioned
        extends AerospikeRepository<MovieDocumentForBatchWriteVersioned, String> {

}
