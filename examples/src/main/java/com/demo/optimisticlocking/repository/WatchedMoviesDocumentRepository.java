package com.demo.optimisticlocking.repository;

import com.demo.optimisticlocking.entity.WatchedMoviesDocument;
import org.springframework.data.aerospike.repository.AerospikeRepository;

public interface WatchedMoviesDocumentRepository extends AerospikeRepository<WatchedMoviesDocument, String> {

}
