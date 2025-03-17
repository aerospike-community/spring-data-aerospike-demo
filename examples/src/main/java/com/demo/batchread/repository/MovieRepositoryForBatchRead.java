package com.demo.batchread.repository;

import com.demo.batchread.entity.MovieDocumentForBatchRead;
import org.springframework.data.aerospike.repository.AerospikeRepository;

public interface MovieRepositoryForBatchRead extends AerospikeRepository<MovieDocumentForBatchRead, String> {

}
