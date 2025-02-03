package com.example.demo.optimisticlocking.repository;

import com.example.demo.optimisticlocking.entity.WatchedMoviesDocument;
import org.springframework.data.aerospike.repository.AerospikeRepository;

public interface WatchedMoviesDocumentRepository extends AerospikeRepository<WatchedMoviesDocument, String> {
}
