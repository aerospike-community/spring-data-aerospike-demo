package com.demo.reactive.optimisticlocking.repository;

import com.demo.reactive.optimisticlocking.entity.WatchedMoviesDocument;
import org.springframework.data.aerospike.repository.ReactiveAerospikeRepository;

public interface ReactiveWatchedMoviesDocumentRepository extends ReactiveAerospikeRepository<WatchedMoviesDocument, String> {

}
