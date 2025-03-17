package com.demo.batchwrite.repository;

import com.demo.batchwrite.entity.MovieDocumentForBatchWrite;
import org.springframework.data.aerospike.repository.AerospikeRepository;

public interface MovieRepositoryForBatchWrite extends AerospikeRepository<MovieDocumentForBatchWrite, String> {

}
